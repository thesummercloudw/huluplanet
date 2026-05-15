package com.catplanet.module.hospital.service;

import com.catplanet.module.hospital.entity.Hospital;
import com.catplanet.module.hospital.entity.HospitalReview;
import com.catplanet.module.hospital.dto.HospitalReviewRequest;
import com.catplanet.module.hospital.vo.HospitalReviewVO;

import java.math.BigDecimal;
import java.util.List;

public interface HospitalService {

    List<Hospital> listNearby(BigDecimal lat, BigDecimal lng, int radius, String type, int page, int size);

    Hospital getById(Long hospitalId);

    List<HospitalReviewVO> listReviews(Long hospitalId, int page, int size);

    HospitalReview createReview(HospitalReviewRequest request, Long userId);
}
