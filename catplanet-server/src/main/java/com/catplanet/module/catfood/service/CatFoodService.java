package com.catplanet.module.catfood.service;

import com.catplanet.module.catfood.entity.CatFood;
import com.catplanet.module.catfood.entity.PgcReview;

import java.util.List;

public interface CatFoodService {

    List<CatFood> list(String foodType, String ageStage, String brand, String keyword, int page, int size);

    CatFood getById(Long foodId);

    PgcReview getPgcReview(Long foodId);

    List<PgcReview> listPgcReviews(int limit);
}
