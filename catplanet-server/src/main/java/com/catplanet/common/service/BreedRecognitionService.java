package com.catplanet.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 猫咪品种识别服务 - 基于阿里云通义千问VL (OpenAI兼容接口)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BreedRecognitionService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Value("${catplanet.qwen-vl.api-key:}")
    private String apiKey;

    @Value("${catplanet.qwen-vl.model:qwen-vl-plus}")
    private String model;

    @Value("${catplanet.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 根据本地图片文件识别猫咪品种
     * @param filename 文件名（uploads目录下的文件）
     * @return 识别到的品种名称，识别失败返回空字符串
     */
    public String recognizeBreed(String filename) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("通义千问VL API Key未配置，跳过品种识别");
            return "";
        }

        try {
            // 读取图片文件并转为Base64 data URL
            Path filePath = Paths.get(uploadPath).toAbsolutePath().normalize().resolve(filename);
            if (!Files.exists(filePath)) {
                log.warn("图片文件不存在: {}", filePath);
                return "";
            }
            byte[] imageBytes = Files.readAllBytes(filePath);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 推断MIME类型
            String mimeType = inferMimeType(filename);
            String dataUrl = "data:" + mimeType + ";base64," + base64Image;

            // 构建OpenAI兼容格式的请求体
            String requestBody = buildRequestBody(dataUrl);

            // 调用通义千问VL API
            String response = webClient.post()
                    .uri(DASHSCOPE_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseBreedFromResponse(response);
        } catch (Exception e) {
            log.error("品种识别异常", e);
            return "";
        }
    }

    /**
     * 构建OpenAI兼容格式的请求JSON
     */
    private String buildRequestBody(String imageDataUrl) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", model);
            root.put("max_tokens", 100);

            ArrayNode messages = root.putArray("messages");
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");

            ArrayNode content = userMsg.putArray("content");

            // 图片内容
            ObjectNode imageContent = content.addObject();
            imageContent.put("type", "image_url");
            ObjectNode imageUrl = imageContent.putObject("image_url");
            imageUrl.put("url", imageDataUrl);

            // 文本提示词
            ObjectNode textContent = content.addObject();
            textContent.put("type", "text");
            textContent.put("text", "请识别这张图片中猫咪的品种。只需要回答品种名称，不要其他内容。如果图片中没有猫或无法识别，请回答未知。");

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }

    /**
     * 从通义千问VL响应中解析品种名称
     */
    private String parseBreedFromResponse(String response) {
        try {
            JsonNode json = objectMapper.readTree(response);

            // 检查是否有错误
            if (json.has("error")) {
                log.warn("通义千问VL返回错误: {}", json.get("error"));
                return "";
            }

            // 解析 choices[0].message.content
            JsonNode choices = json.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                log.warn("通义千问VL响应无choices: {}", response);
                return "";
            }

            String content = choices.get(0)
                    .get("message")
                    .get("content")
                    .asText("")
                    .trim();

            // 清理结果：去掉可能的标点、多余文字
            content = content.replace("。", "").replace(".", "").trim();

            if (content.isEmpty() || content.contains("未知") || content.contains("无法识别")) {
                log.info("品种识别结果: 未能识别");
                return "";
            }

            log.info("品种识别结果: {}", content);
            return content;
        } catch (Exception e) {
            log.error("解析品种识别响应失败", e);
            return "";
        }
    }

    /**
     * 根据文件扩展名推断MIME类型
     */
    private String inferMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}
