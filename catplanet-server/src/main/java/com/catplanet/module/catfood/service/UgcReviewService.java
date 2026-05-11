package com.catplanet.module.catfood.service;

import com.catplanet.module.catfood.dto.UgcReviewRequest;
import com.catplanet.module.catfood.entity.UgcShortReview;

import java.util.List;

public interface UgcReviewService {

    UgcShortReview create(UgcReviewRequest request, Long userId);

    List<UgcShortReview> listByFood(Long foodId, int page, int size);

    List<UgcShortReview> listByUser(Long userId);

    void delete(Long reviewId, Long userId);
}
