package com.catplanet.module.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.module.hospital.dto.HospitalReviewRequest;
import com.catplanet.module.hospital.entity.Hospital;
import com.catplanet.module.hospital.entity.HospitalReview;
import com.catplanet.module.hospital.mapper.HospitalMapper;
import com.catplanet.module.hospital.mapper.HospitalReviewMapper;
import com.catplanet.module.hospital.service.HospitalService;
import com.catplanet.module.hospital.vo.HospitalReviewVO;
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
public class HospitalServiceImpl implements HospitalService {

    private final HospitalMapper hospitalMapper;
    private final HospitalReviewMapper hospitalReviewMapper;
    private final UserService userService;

    @Override
    public List<Hospital> listNearby(BigDecimal lat, BigDecimal lng, int radius, String type, int page, int size) {
        // 简化版：按矩形范围筛选（1度≈111km，radius单位km）
        BigDecimal delta = BigDecimal.valueOf(radius).divide(BigDecimal.valueOf(111), 6, RoundingMode.HALF_UP);
        BigDecimal minLat = lat.subtract(delta);
        BigDecimal maxLat = lat.add(delta);
        BigDecimal minLng = lng.subtract(delta);
        BigDecimal maxLng = lng.add(delta);

        int offset = (page - 1) * size;
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<Hospital>()
                .between(Hospital::getLat, minLat, maxLat)
                .between(Hospital::getLng, minLng, maxLng);

        // 按类型筛选：hospital/petstore，不传则查全部
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Hospital::getType, type);
        }

        wrapper.orderByDesc(Hospital::getAvgScore)
               .last("LIMIT " + offset + "," + size);

        return hospitalMapper.selectList(wrapper);
    }

    @Override
    public Hospital getById(Long hospitalId) {
        return hospitalMapper.selectById(hospitalId);
    }

    @Override
    public List<HospitalReviewVO> listReviews(Long hospitalId, int page, int size) {
        int offset = (page - 1) * size;
        List<HospitalReview> reviews = hospitalReviewMapper.selectList(
                new LambdaQueryWrapper<HospitalReview>()
                        .eq(HospitalReview::getHospitalId, hospitalId)
                        .eq(HospitalReview::getAuditStatus, "approved")
                        .orderByDesc(HospitalReview::getCreatedAt)
                        .last("LIMIT " + offset + "," + size));

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户信息
        List<Long> userIds = reviews.stream()
                .map(HospitalReview::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        return reviews.stream().map(r -> {
            HospitalReviewVO vo = new HospitalReviewVO();
            vo.setReviewId(r.getReviewId());
            vo.setHospitalId(r.getHospitalId());
            vo.setUserId(r.getUserId());
            vo.setScore(r.getScore());
            vo.setContent(r.getContent());
            vo.setServiceTags(r.getServiceTags());
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
    public HospitalReview createReview(HospitalReviewRequest request, Long userId) {
        HospitalReview review = new HospitalReview();
        review.setHospitalId(request.getHospitalId());
        review.setUserId(userId);
        review.setScore(request.getScore());
        review.setContent(request.getContent());
        review.setServiceTags(request.getServiceTags());
        review.setImages(request.getImages());
        review.setAuditStatus("approved");
        hospitalReviewMapper.insert(review);

        updateHospitalScore(request.getHospitalId());
        return review;
    }

    private void updateHospitalScore(Long hospitalId) {
        List<HospitalReview> approved = hospitalReviewMapper.selectList(
                new LambdaQueryWrapper<HospitalReview>()
                        .eq(HospitalReview::getHospitalId, hospitalId)
                        .eq(HospitalReview::getAuditStatus, "approved"));

        Hospital hospital = hospitalMapper.selectById(hospitalId);
        if (hospital != null) {
            int count = approved.size();
            hospital.setReviewCount(count);
            if (count > 0) {
                double avg = approved.stream().mapToInt(HospitalReview::getScore).average().orElse(0);
                hospital.setAvgScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            } else {
                hospital.setAvgScore(BigDecimal.ZERO);
            }
            hospitalMapper.updateById(hospital);
        }
    }
}
