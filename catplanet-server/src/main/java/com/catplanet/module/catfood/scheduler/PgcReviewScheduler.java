package com.catplanet.module.catfood.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.service.ImageDownloadService;
import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.entity.PgcReview;
import com.catplanet.module.catfood.mapper.CatFoodMapper;
import com.catplanet.module.catfood.mapper.PgcReviewMapper;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 精选测评定时拉取任务
 * 每天通过AI接口生成猫粮精选测评内容，并从网络拉取首图
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PgcReviewScheduler {

    private final CatFoodMapper catFoodMapper;
    private final PgcReviewMapper pgcReviewMapper;
    private final ImageDownloadService imageDownloadService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Value("${catplanet.qwen-vl.api-key:}")
    private String apiKey;

    @Value("${catplanet.pgc-review.daily-count:2}")
    private int dailyCount;

    /**
     * 每天凌晨4点执行：为没有PGC测评的猫粮生成精选测评
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void generateDailyReviews() {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("[PGC测评] API Key未配置，跳过");
            return;
        }

        // 找出还没有PGC测评的猫粮（优先评分高的）
        List<CatFood> allFoods = catFoodMapper.selectList(
                new LambdaQueryWrapper<CatFood>()
                        .orderByDesc(CatFood::getAvgScore)
                        .last("LIMIT 50"));

        List<CatFood> foodsNeedReview = allFoods.stream()
                .filter(food -> {
                    Long count = pgcReviewMapper.selectCount(
                            new LambdaQueryWrapper<PgcReview>()
                                    .eq(PgcReview::getFoodId, food.getFoodId()));
                    return count == 0;
                })
                .limit(dailyCount)
                .toList();

        if (foodsNeedReview.isEmpty()) {
            log.info("[PGC测评] 所有猫粮已有测评，无需生成");
            return;
        }

        log.info("[PGC测评] 将为 {} 款猫粮生成精选测评", foodsNeedReview.size());

        for (CatFood food : foodsNeedReview) {
            try {
                generateReviewForFood(food);
                // 限流：间隔5秒
                Thread.sleep(5000);
            } catch (Exception e) {
                log.error("[PGC测评] 生成失败: {} {}, error={}", food.getBrand(), food.getName(), e.getMessage());
            }
        }
    }

    private void generateReviewForFood(CatFood food) {
        // Step 1: 通过AI生成测评内容
        String reviewJson = callAiForReview(food);
        if (reviewJson == null) {
            log.warn("[PGC测评] AI返回空: {} {}", food.getBrand(), food.getName());
            return;
        }

        // Step 2: 解析并清洗数据
        PgcReview review = parseReviewData(reviewJson, food.getFoodId());
        if (review == null) {
            log.warn("[PGC测评] 解析失败: {} {}", food.getBrand(), food.getName());
            return;
        }

        // Step 3: 从网络拉取首图
        String coverUrl = searchCoverImage(food.getBrand(), food.getName());
        if (coverUrl != null) {
            String localPath = imageDownloadService.downloadImage(coverUrl);
            if (localPath != null) {
                review.setCover(localPath);
            }
        }

        // 如果没有拉取到首图，用猫粮自身的图片作为封面
        if (review.getCover() == null || review.getCover().isBlank()) {
            if (food.getImage() != null && !food.getImage().isBlank()) {
                review.setCover(food.getImage());
            } else {
                log.warn("[PGC测评] 无可用首图，跳过: {} {}", food.getBrand(), food.getName());
                return;
            }
        }

        // Step 4: 入库
        review.setPublishedAt(LocalDateTime.now());
        pgcReviewMapper.insert(review);
        log.info("[PGC测评] 生成成功: {} {} -> reviewId={}", food.getBrand(), food.getName(), review.getReviewId());
    }

    /**
     * 调用AI生成测评JSON
     */
    private String callAiForReview(CatFood food) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", "qwen-turbo");
            root.put("max_tokens", 1000);

            ArrayNode messages = root.putArray("messages");

            ObjectNode systemMsg = messages.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "你是一位专业的猫粮评测师，请根据给定的猫粮信息生成一篇专业测评。" +
                    "输出必须是严格的JSON格式，包含以下字段：\n" +
                    "- title: 测评标题（如\"XX品牌YY猫粮深度测评\"）\n" +
                    "- contentMd: 测评正文（Markdown格式，200-400字，包含成分分析、营养值、适口性、性价比等）\n" +
                    "- scoreIngredient: 原料评分(1-10整数)\n" +
                    "- scoreNutrition: 营养评分(1-10整数)\n" +
                    "- scoreValue: 性价比评分(1-10整数)\n" +
                    "- scorePalatability: 适口性评分(1-10整数)\n" +
                    "- scoreSafety: 安全性评分(1-10整数)\n" +
                    "只返回JSON，不要其他内容。");

            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");

            StringBuilder prompt = new StringBuilder();
            prompt.append("请为以下猫粮生成测评：\n");
            prompt.append("品牌：").append(food.getBrand()).append("\n");
            prompt.append("名称：").append(food.getName()).append("\n");
            prompt.append("类型：").append(food.getFoodType()).append("\n");
            prompt.append("适用阶段：").append(food.getAgeStage()).append("\n");
            if (food.getProteinPct() != null) {
                prompt.append("蛋白质：").append(food.getProteinPct()).append("%\n");
            }
            if (food.getFatPct() != null) {
                prompt.append("脂肪：").append(food.getFatPct()).append("%\n");
            }
            if (food.getIngredientsSummary() != null) {
                prompt.append("配料概述：").append(food.getIngredientsSummary()).append("\n");
            }
            if (food.getPriceRange() != null) {
                prompt.append("价格区间：").append(food.getPriceRange()).append("\n");
            }
            userMsg.put("content", prompt.toString());

            String requestBody = objectMapper.writeValueAsString(root);

            return webClient.post()
                    .uri(DASHSCOPE_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("[PGC测评] AI调用异常", e);
            return null;
        }
    }

    /**
     * 解析AI返回的测评JSON
     */
    private PgcReview parseReviewData(String response, Long foodId) {
        try {
            JsonNode json = objectMapper.readTree(response);
            if (json.has("error")) {
                log.warn("[PGC测评] AI错误: {}", json.get("error"));
                return null;
            }

            JsonNode choices = json.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                return null;
            }

            String content = choices.get(0).get("message").get("content").asText("").trim();

            // 清理可能的markdown代码块包裹
            if (content.startsWith("```json")) {
                content = content.substring(7);
            } else if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            JsonNode reviewNode = objectMapper.readTree(content);

            PgcReview review = new PgcReview();
            review.setFoodId(foodId);
            review.setTitle(reviewNode.path("title").asText("猫粮测评"));
            review.setContentMd(reviewNode.path("contentMd").asText(""));
            review.setScoreIngredient(clampScore(reviewNode.path("scoreIngredient").asInt(7)));
            review.setScoreNutrition(clampScore(reviewNode.path("scoreNutrition").asInt(7)));
            review.setScoreValue(clampScore(reviewNode.path("scoreValue").asInt(7)));
            review.setScorePalatability(clampScore(reviewNode.path("scorePalatability").asInt(7)));
            review.setScoreSafety(clampScore(reviewNode.path("scoreSafety").asInt(7)));

            // 数据清洗：标题不能为空，内容不能太短
            if (review.getTitle().isBlank() || review.getContentMd().length() < 50) {
                log.warn("[PGC测评] 数据清洗不通过：标题或内容不足");
                return null;
            }

            return review;
        } catch (Exception e) {
            log.error("[PGC测评] 解析测评JSON失败", e);
            return null;
        }
    }

    /**
     * 搜索测评首图
     */
    private String searchCoverImage(String brand, String name) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", "qwen-turbo");
            root.put("max_tokens", 200);

            ArrayNode messages = root.putArray("messages");

            ObjectNode systemMsg = messages.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是图片搜索助手。用户给你一个猫粮品牌和名称，" +
                    "请返回一张该产品的高清产品图片URL。只返回一个URL，不要其他文字。" +
                    "URL必须是以https开头的有效图片地址。如果找不到返回空字符串。");

            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", brand + " " + name + " 产品图片");

            String requestBody = objectMapper.writeValueAsString(root);

            String response = webClient.post()
                    .uri(DASHSCOPE_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);
            JsonNode choices = json.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                return null;
            }

            String url = choices.get(0).get("message").get("content").asText("").trim();
            return url.startsWith("http") ? url.split("\\s")[0] : null;
        } catch (Exception e) {
            log.error("[PGC测评] 首图搜索失败: {} {}", brand, name);
            return null;
        }
    }

    private int clampScore(int score) {
        return Math.max(1, Math.min(10, score));
    }
}
