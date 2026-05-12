package com.catplanet.common.controller;

import com.catplanet.common.result.Result;
import com.catplanet.common.service.BreedRecognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 猫咪品种识别接口
 */
@RestController
@RequestMapping("/api/breed-recognition")
@RequiredArgsConstructor
public class BreedRecognitionController {

    private final BreedRecognitionService breedRecognitionService;

    /**
     * 根据已上传的图片识别猫咪品种
     * @param body 请求体，包含 imageUrl 字段（上传后返回的完整URL）
     * @return 识别到的品种名称
     */
    @PostMapping
    public Result<Map<String, String>> recognize(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return Result.fail(400, "图片URL不能为空");
        }

        // 从URL中提取文件名
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        String breed = breedRecognitionService.recognizeBreed(filename);

        return Result.ok(Map.of("breed", breed));
    }
}
