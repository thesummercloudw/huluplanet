package com.catplanet.module.catfood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.catfood.dto.UgcReviewRequest;
import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.entity.UgcShortReview;
import com.catplanet.module.catfood.mapper.CatFoodMapper;
import com.catplanet.module.catfood.mapper.UgcShortReviewMapper;
import com.catplanet.module.catfood.service.UgcReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UgcReviewServiceImpl implements UgcReviewService {

    private final UgcShortReviewMapper ugcShortReviewMapper;
    private final CatFoodMapper catFoodMapper;

    @Override
    public UgcShortReview create(UgcReviewRequest request, Long userId) {
        UgcShortReview review = new UgcShortReview();
        review.setFoodId(request.getFoodId());
        review.setUserId(userId);
        review.setCatId(request.getCatId());
        review.setScore(request.getScore());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setAuditStatus("pending");
        ugcShortReviewMapper.insert(review);

        // 更新猫粮的评分和评价数
        updateFoodScore(request.getFoodId());

        return review;
    }

    @Override
    public List<UgcShortReview> listByFood(Long foodId, int page, int size) {
        int offset = (page - 1) * size;
        return ugcShortReviewMapper.selectList(
                new LambdaQueryWrapper<UgcShortReview>()
                        .eq(UgcShortReview::getFoodId, foodId)
                        .eq(UgcShortReview::getAuditStatus, "approved")
                        .orderByDesc(UgcShortReview::getCreatedAt)
                        .last("LIMIT " + offset + "," + size));
    }

    @Override
    public List<UgcShortReview> listByUser(Long userId) {
        return ugcShortReviewMapper.selectList(
                new LambdaQueryWrapper<UgcShortReview>()
                        .eq(UgcShortReview::getUserId, userId)
                        .orderByDesc(UgcShortReview::getCreatedAt));
    }

    @Override
    public void delete(Long reviewId, Long userId) {
        UgcShortReview review = ugcShortReviewMapper.selectById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        ugcShortReviewMapper.deleteById(reviewId);
        updateFoodScore(review.getFoodId());
    }

    private void updateFoodScore(Long foodId) {
        List<UgcShortReview> approved = ugcShortReviewMapper.selectList(
                new LambdaQueryWrapper<UgcShortReview>()
                        .eq(UgcShortReview::getFoodId, foodId)
                        .eq(UgcShortReview::getAuditStatus, "approved"));

        CatFood food = catFoodMapper.selectById(foodId);
        if (food != null) {
            int count = approved.size();
            food.setReviewCount(count);
            if (count > 0) {
                double avg = approved.stream().mapToInt(UgcShortReview::getScore).average().orElse(0);
                food.setAvgScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            } else {
                food.setAvgScore(BigDecimal.ZERO);
            }
            catFoodMapper.updateById(food);
        }
    }
}
