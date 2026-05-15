package com.catplanet.common.controller;

import com.catplanet.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * 轻量逆地理编码接口（基于腾讯地图WebService API）
 * 不需要登录即可访问
 */
@Slf4j
@RestController
@RequestMapping("/api/public/geocode")
@RequiredArgsConstructor
public class GeocodeController {

    private final WebClient webClient;

    @Value("${catplanet.tencent-lbs.key:}")
    private String lbsKey;

    @GetMapping("/city")
    public Result<Map<String, String>> reverseCity(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        if (lbsKey == null || lbsKey.isBlank() || lbsKey.contains("XXXXX")) {
            // Key未配置，返回空结果（前端静默降级）
            return Result.ok(Map.of());
        }

        try {
            String url = String.format(
                    "https://apis.map.qq.com/ws/geocoder/v1/?location=%f,%f&key=%s&get_poi=0",
                    lat, lng, lbsKey);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 简单解析JSON获取城市
            String city = extractCity(response);
            String district = extractDistrict(response);

            Map<String, String> result = new java.util.HashMap<>();
            if (city != null) result.put("city", city);
            if (district != null) result.put("district", district);
            return Result.ok(result);
        } catch (Exception e) {
            log.warn("逆地理编码失败: lat={}, lng={}, error={}", lat, lng, e.getMessage());
            return Result.ok(Map.of());
        }
    }

    private String extractCity(String json) {
        if (json == null) return null;
        // 匹配 "city":"xxx"
        int idx = json.indexOf("\"city\":");
        if (idx < 0) return null;
        int start = json.indexOf("\"", idx + 7) + 1;
        int end = json.indexOf("\"", start);
        if (start > 0 && end > start) {
            return json.substring(start, end);
        }
        return null;
    }

    private String extractDistrict(String json) {
        if (json == null) return null;
        int idx = json.indexOf("\"district\":");
        if (idx < 0) return null;
        int start = json.indexOf("\"", idx + 11) + 1;
        int end = json.indexOf("\"", start);
        if (start > 0 && end > start) {
            return json.substring(start, end);
        }
        return null;
    }
}
