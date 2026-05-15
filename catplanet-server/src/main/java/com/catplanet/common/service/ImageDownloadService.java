package com.catplanet.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 网络图片下载服务 —— 从URL拉取图片保存到本地 uploads 目录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageDownloadService {

    private final WebClient webClient;

    @Value("${catplanet.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 从网络URL下载图片到本地
     *
     * @param imageUrl 网络图片URL
     * @return 本地相对路径 /uploads/xxx.jpg，下载失败返回 null
     */
    public String downloadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        try {
            // 下载图片字节
            byte[] imageBytes = webClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                log.warn("[图片下载] 下载为空: {}", imageUrl);
                return null;
            }

            // 确定文件扩展名
            String ext = guessExtension(imageUrl);

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 确保目录存在
            Path dirPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);

            // 写入文件
            Path filePath = dirPath.resolve(filename);
            Files.write(filePath, imageBytes);

            log.info("[图片下载] 成功: {} -> {}", imageUrl, filename);
            return "/uploads/" + filename;
        } catch (IOException e) {
            log.error("[图片下载] IO异常: url={}, error={}", imageUrl, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[图片下载] 下载失败: url={}, error={}", imageUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 根据URL推断文件扩展名
     */
    private String guessExtension(String url) {
        String lower = url.toLowerCase();
        // 去掉查询参数
        int queryIdx = lower.indexOf('?');
        if (queryIdx > 0) {
            lower = lower.substring(0, queryIdx);
        }
        if (lower.endsWith(".png")) return ".png";
        if (lower.endsWith(".webp")) return ".webp";
        if (lower.endsWith(".gif")) return ".gif";
        if (lower.endsWith(".jpeg")) return ".jpeg";
        return ".jpg";
    }
}
