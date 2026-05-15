package com.catplanet.module.hospital.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.hospital.dto.HospitalReviewRequest;
import com.catplanet.module.hospital.entity.Hospital;
import com.catplanet.module.hospital.entity.HospitalReview;
import com.catplanet.module.hospital.service.HospitalService;
import com.catplanet.module.hospital.vo.HospitalReviewVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public Result<List<Hospital>> listNearby(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5") int radius,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(hospitalService.listNearby(lat, lng, radius, type, page, size));
    }

    @GetMapping("/{hospitalId}")
    public Result<Map<String, Object>> detail(@PathVariable Long hospitalId) {
        Hospital hospital = hospitalService.getById(hospitalId);
        List<HospitalReviewVO> reviews = hospitalService.listReviews(hospitalId, 1, 10);

        Map<String, Object> result = new HashMap<>();
        result.put("hospital", hospital);
        result.put("reviews", reviews);
        return Result.ok(result);
    }

    @GetMapping("/{hospitalId}/reviews")
    public Result<List<HospitalReviewVO>> reviews(
            @PathVariable Long hospitalId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(hospitalService.listReviews(hospitalId, page, size));
    }

    @PostMapping("/reviews")
    public Result<HospitalReview> createReview(@Valid @RequestBody HospitalReviewRequest request) {
        Long userId = UserContext.getUserId();
        return Result.ok(hospitalService.createReview(request, userId));
    }
}
