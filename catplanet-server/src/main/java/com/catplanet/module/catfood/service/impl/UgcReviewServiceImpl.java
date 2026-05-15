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
import com.catplanet.module.catfood.vo.UgcReviewVO;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UgcReviewServiceImpl implements UgcReviewService {

    private final UgcShortReviewMapper ugcShortReviewMapper;
    private final CatFoodMapper catFoodMapper;
    private final UserService userService;

    @Override
    public UgcShortReview create(UgcReviewRequest request, Long userId) {
        UgcShortReview review = new UgcShortReview();
        review.setFoodId(request.getFoodId());
        review.setUserId(userId);
        review.setCatId(request.getCatId());
        review.setScore(request.getScore());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setAuditStatus("approved");
        ugcShortReviewMapper.insert(review);

        // 更新猫粮的评分和评价数
        updateFoodScore(request.getFoodId());

        return review;
    }

    @Override
    public List<UgcReviewVO> listByFood(Long foodId, int page, int size) {
        int offset = (page - 1) * size;
        List<UgcShortReview> reviews = ugcShortReviewMapper.selectList(
                new LambdaQueryWrapper<UgcShortReview>()
                        .eq(UgcShortReview::getFoodId, foodId)
                        .eq(UgcShortReview::getAuditStatus, "approved")
                        .orderByDesc(UgcShortReview::getCreatedAt)
                        .last("LIMIT " + offset + "," + size));

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户信息
        List<Long> userIds = reviews.stream()
                .map(UgcShortReview::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        // 转换为VO
        return reviews.stream().map(r -> {
            UgcReviewVO vo = new UgcReviewVO();
            vo.setReviewId(r.getReviewId());
            vo.setFoodId(r.getFoodId());
            vo.setUserId(r.getUserId());
            vo.setCatId(r.getCatId());
            vo.setScore(r.getScore());
            vo.setContent(r.getContent());
            vo.setImages(r.getImages());
            vo.setCreatedAt(r.getCreatedAt());

            User user = userMap.get(r.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
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
