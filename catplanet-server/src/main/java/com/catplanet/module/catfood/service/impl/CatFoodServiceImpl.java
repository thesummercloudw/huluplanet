package com.catplanet.module.catfood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.entity.PgcReview;
import com.catplanet.module.catfood.mapper.CatFoodMapper;
import com.catplanet.module.catfood.mapper.PgcReviewMapper;
import com.catplanet.module.catfood.service.CatFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatFoodServiceImpl implements CatFoodService {

    private final CatFoodMapper catFoodMapper;
    private final PgcReviewMapper pgcReviewMapper;

    @Override
    public List<CatFood> list(String foodType, String ageStage, String brand, String keyword, int page, int size) {
        LambdaQueryWrapper<CatFood> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(foodType)) {
            wrapper.eq(CatFood::getFoodType, foodType);
        }
        if (StringUtils.hasText(ageStage)) {
            wrapper.eq(CatFood::getAgeStage, ageStage);
        }
        if (StringUtils.hasText(brand)) {
            wrapper.like(CatFood::getBrand, brand);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(CatFood::getName, keyword)
                    .or().like(CatFood::getBrand, keyword));
        }
        wrapper.orderByDesc(CatFood::getAvgScore);
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + offset + "," + size);
        return catFoodMapper.selectList(wrapper);
    }

    @Override
    public CatFood getById(Long foodId) {
        return catFoodMapper.selectById(foodId);
    }

    @Override
    public PgcReview getPgcReview(Long foodId) {
        return pgcReviewMapper.selectOne(
                new LambdaQueryWrapper<PgcReview>()
                        .eq(PgcReview::getFoodId, foodId)
                        .isNotNull(PgcReview::getPublishedAt)
                        .orderByDesc(PgcReview::getPublishedAt)
                        .last("LIMIT 1"));
    }

    @Override
    public List<PgcReview> listPgcReviews(int limit) {
        return pgcReviewMapper.selectList(
                new LambdaQueryWrapper<PgcReview>()
                        .isNotNull(PgcReview::getPublishedAt)
                        .orderByDesc(PgcReview::getPublishedAt)
                        .last("LIMIT " + limit));
    }
}
