package com.catplanet.module.catfood.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.catfood.dto.UgcReviewRequest;
import com.catplanet.module.catfood.entity.UgcShortReview;
import com.catplanet.module.catfood.service.UgcReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catfood/ugc")
@RequiredArgsConstructor
public class UgcReviewController {

    private final UgcReviewService ugcReviewService;

    @PostMapping
    public Result<UgcShortReview> create(@Valid @RequestBody UgcReviewRequest request) {
        Long userId = UserContext.getUserId();
        return Result.ok(ugcReviewService.create(request, userId));
    }

    @GetMapping("/mine")
    public Result<List<UgcShortReview>> myReviews() {
        Long userId = UserContext.getUserId();
        return Result.ok(ugcReviewService.listByUser(userId));
    }

    @DeleteMapping("/{reviewId}")
    public Result<Void> delete(@PathVariable Long reviewId) {
        Long userId = UserContext.getUserId();
        ugcReviewService.delete(reviewId, userId);
        return Result.ok();
    }
}
