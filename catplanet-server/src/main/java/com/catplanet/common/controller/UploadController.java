package com.catplanet.common.controller;

import com.catplanet.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${catplanet.upload.path:./uploads}")
    private String uploadPath;

    @Value("${catplanet.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                              HttpServletRequest request) throws IOException {
        if (file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.fail(400, "仅支持图片文件");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;

        // 确保目录存在（转为绝对路径，避免相对路径解析到 Tomcat 临时目录）
        Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 保存文件
        Path filePath = dir.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 构建完整访问URL（小程序需要完整地址）
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String url = baseUrl + urlPrefix + "/" + newFilename;
        return Result.ok(Map.of("url", url));
    }
}
