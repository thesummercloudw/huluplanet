package com.catplanet.common.controller;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片文件服务控制器
 * - 支持缩略图：?w=200 按宽度等比缩放
 * - 强缓存：图片文件名含 UUID 且不可变，缓存 1 年 + immutable
 * - ETag：基于文件名+尺寸生成，支持 304 Not Modified
 */
@RestController
@RequestMapping("/api/public/image")
public class ImageController {

    @Value("${catplanet.upload.path:./uploads}")
    private String uploadPath;

    /** 缩略图内存缓存（避免反复压缩，小项目内存占用可控） */
    private final ConcurrentHashMap<String, byte[]> thumbCache = new ConcurrentHashMap<>();

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            @RequestParam(value = "w", required = false) Integer width) {
        try {
            Path filePath = Paths.get(uploadPath).toAbsolutePath().normalize().resolve(filename);

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            String contentType = determineContentType(filename);
            // ETag 基于文件名 + 宽度参数
            String etag = "\"" + filename.hashCode() + "-" + (width != null ? width : "full") + "\"";

            // 强缓存 365 天 + immutable（UUID文件名不可变）
            CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365))
                    .cachePublic()
                    .immutable();

            // 无需缩略图，直接返回原图
            if (width == null || width <= 0) {
                Resource resource = new UrlResource(filePath.toUri());
                return ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .eTag(etag)
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            }

            // 限制缩略图宽度范围，避免恶意请求
            int w = Math.min(width, 1200);

            // 从缓存获取或生成缩略图
            String cacheKey = filename + "_w" + w;
            byte[] thumbBytes = thumbCache.computeIfAbsent(cacheKey, k -> {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Thumbnails.of(filePath.toFile())
                            .width(w)
                            .outputQuality(0.85)
                            .toOutputStream(out);
                    return out.toByteArray();
                } catch (IOException e) {
                    return null;
                }
            });

            if (thumbBytes == null) {
                // 缩略图生成失败，返回原图
                thumbCache.remove(cacheKey);
                Resource resource = new UrlResource(filePath.toUri());
                return ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .eTag(etag)
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            }

            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .eTag(etag)
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .contentLength(thumbBytes.length)
                    .body(new ByteArrayResource(thumbBytes));

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String determineContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
