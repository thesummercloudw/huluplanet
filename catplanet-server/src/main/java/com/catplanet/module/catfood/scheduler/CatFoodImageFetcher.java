package com.catplanet.module.catfood.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.service.ImageDownloadService;
import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.mapper.CatFoodMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * 猫粮图片拉取任务
 * 对没有本地图片的猫粮，通过AI接口搜索产品图片URL并下载到本地
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatFoodImageFetcher {

    private final CatFoodMapper catFoodMapper;
    private final ImageDownloadService imageDownloadService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Value("${catplanet.qwen-vl.api-key:}")
    private String apiKey;

    /**
     * 每天凌晨3点执行：为缺失图片的猫粮拉取网络产品图
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void fetchMissingImages() {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("[猫粮图片] API Key未配置，跳过");
            return;
        }

        // 查找没有图片或图片为空的猫粮
        List<CatFood> foodsNeedImage = catFoodMapper.selectList(
                new LambdaQueryWrapper<CatFood>()
                        .and(w -> w.isNull(CatFood::getImage)
                                .or().eq(CatFood::getImage, ""))
                        .last("LIMIT 10"));

        if (foodsNeedImage.isEmpty()) {
            log.info("[猫粮图片] 所有猫粮已有图片，无需拉取");
            return;
        }

        log.info("[猫粮图片] 发现 {} 条缺失图片的猫粮，开始拉取", foodsNeedImage.size());

        for (CatFood food : foodsNeedImage) {
            try {
                String imageUrl = searchProductImage(food.getBrand(), food.getName());
                if (imageUrl != null && !imageUrl.isBlank()) {
                    String localPath = imageDownloadService.downloadImage(imageUrl);
                    if (localPath != null) {
                        food.setImage(localPath);
                        catFoodMapper.updateById(food);
                        log.info("[猫粮图片] 成功: {} {} -> {}", food.getBrand(), food.getName(), localPath);
                    }
                }
                // 限流：每次间隔2秒
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("[猫粮图片] 处理失败: {} {}, error={}", food.getBrand(), food.getName(), e.getMessage());
            }
        }
    }

    /**
     * 通过AI接口获取猫粮产品图片URL
     */
    private String searchProductImage(String brand, String name) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", "qwen-turbo");
            root.put("max_tokens", 200);

            ArrayNode messages = root.putArray("messages");

            ObjectNode systemMsg = messages.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个猫粮产品图片搜索助手。用户会给你一个猫粮品牌和名称，" +
                    "请返回该产品的官方产品图片URL。只返回一个有效的图片URL，不要其他内容。" +
                    "图片URL必须是以http或https开头的完整URL，优先选择京东、天猫、官网的产品主图。" +
                    "如果找不到，返回空字符串。");

            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", "请给我猫粮产品\"" + brand + " " + name + "\"的产品图片URL");

            String requestBody = objectMapper.writeValueAsString(root);

            String response = webClient.post()
                    .uri(DASHSCOPE_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseImageUrl(response);
        } catch (Exception e) {
            log.error("[猫粮图片] AI搜索失败: {} {}, error={}", brand, name, e.getMessage());
            return null;
        }
    }

    private String parseImageUrl(String response) {
        try {
            JsonNode json = objectMapper.readTree(response);
            if (json.has("error")) {
                log.warn("[猫粮图片] AI返回错误: {}", json.get("error"));
                return null;
            }

            JsonNode choices = json.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                return null;
            }

            String content = choices.get(0).get("message").get("content").asText("").trim();

            // 提取URL（AI可能返回额外文字）
            if (content.startsWith("http")) {
                // 截取到第一个空格或换行
                int endIdx = content.indexOf(' ');
                if (endIdx < 0) endIdx = content.indexOf('\n');
                if (endIdx < 0) endIdx = content.length();
                return content.substring(0, endIdx).trim();
            }

            return null;
        } catch (Exception e) {
            log.error("[猫粮图片] 解析响应失败", e);
            return null;
        }
    }
}
