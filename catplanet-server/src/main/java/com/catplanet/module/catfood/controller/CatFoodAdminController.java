package com.catplanet.module.catfood.controller;

import com.catplanet.common.result.Result;
import com.catplanet.module.catfood.scheduler.CatFoodImageFetcher;
import com.catplanet.module.catfood.scheduler.PgcReviewScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 猫粮管理接口（内部运维用）
 * 手动触发图片拉取和测评生成
 */
@RestController
@RequestMapping("/api/public/catfood-admin")
@RequiredArgsConstructor
public class CatFoodAdminController {

    private final CatFoodImageFetcher imageFetcher;
    private final PgcReviewScheduler pgcReviewScheduler;

    /**
     * 手动触发猫粮图片拉取
     */
    @PostMapping("/fetch-images")
    public Result<String> fetchImages() {
        imageFetcher.fetchMissingImages();
        return Result.ok("猫粮图片拉取任务已触发");
    }

    /**
     * 手动触发精选测评生成
     */
    @PostMapping("/generate-reviews")
    public Result<String> generateReviews() {
        pgcReviewScheduler.generateDailyReviews();
        return Result.ok("精选测评生成任务已触发");
    }
}
