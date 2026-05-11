package com.catplanet.module.catfood.controller;

import com.catplanet.common.result.Result;
import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.entity.PgcReview;
import com.catplanet.module.catfood.entity.UgcShortReview;
import com.catplanet.module.catfood.service.CatFoodService;
import com.catplanet.module.catfood.service.UgcReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catfood")
@RequiredArgsConstructor
public class CatFoodController {

    private final CatFoodService catFoodService;
    private final UgcReviewService ugcReviewService;

    @GetMapping
    public Result<List<CatFood>> list(
            @RequestParam(required = false) String foodType,
            @RequestParam(required = false) String ageStage,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(catFoodService.list(foodType, ageStage, brand, page, size));
    }

    @GetMapping("/{foodId}")
    public Result<Map<String, Object>> detail(@PathVariable Long foodId) {
        CatFood food = catFoodService.getById(foodId);
        if (food == null) {
            return Result.ok(null);
        }
        PgcReview pgc = catFoodService.getPgcReview(foodId);
        List<UgcShortReview> ugcList = ugcReviewService.listByFood(foodId, 1, 10);

        Map<String, Object> result = new HashMap<>();
        result.put("food", food);
        result.put("pgcReview", pgc);
        result.put("ugcReviews", ugcList);
        return Result.ok(result);
    }

    @GetMapping("/{foodId}/reviews")
    public Result<List<UgcShortReview>> listReviews(
            @PathVariable Long foodId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(ugcReviewService.listByFood(foodId, page, size));
    }

    @GetMapping("/pgc")
    public Result<List<PgcReview>> listPgcReviews(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(catFoodService.listPgcReviews(limit));
    }
}
