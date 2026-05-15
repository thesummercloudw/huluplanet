package com.catplanet.module.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.adoption.dto.AdoptionApplyRequest;
import com.catplanet.module.adoption.dto.AdoptionPublishRequest;
import com.catplanet.module.adoption.entity.AdoptionApplication;
import com.catplanet.module.adoption.entity.AdoptionCat;
import com.catplanet.module.adoption.mapper.AdoptionApplicationMapper;
import com.catplanet.module.adoption.mapper.AdoptionCatMapper;
import com.catplanet.module.adoption.service.AdoptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionCatMapper adoptionCatMapper;
    private final AdoptionApplicationMapper applicationMapper;

    @Override
    public List<AdoptionCat> listAvailable(String city, int page, int size) {
        LambdaQueryWrapper<AdoptionCat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionCat::getStatus, "available");
        if (StringUtils.hasText(city)) {
            wrapper.eq(AdoptionCat::getCity, city);
        }
        wrapper.orderByDesc(AdoptionCat::getCreatedAt);
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + offset + "," + size);
        return adoptionCatMapper.selectList(wrapper);
    }

    @Override
    public AdoptionCat getById(Long adoptId) {
        AdoptionCat cat = adoptionCatMapper.selectById(adoptId);
        if (cat != null) {
            // 隐藏联系方式（仅运营可见）
            cat.setContactMethod(null);
        }
        return cat;
    }

    @Override
    public AdoptionCat publish(AdoptionPublishRequest request, Long userId) {
        AdoptionCat cat = new AdoptionCat();
        cat.setName(request.getName());
        cat.setCover(request.getCover());
        cat.setImages(request.getImages());
        cat.setGender(request.getGender() != null ? request.getGender() : "unknown");
        cat.setAgeEstimate(request.getAgeEstimate());
        cat.setBreedEstimate(request.getBreedEstimate());
        cat.setCity(request.getCity());
        cat.setProvince(request.getProvince());
        cat.setDistrict(request.getDistrict());
        cat.setLat(request.getLat());
        cat.setLng(request.getLng());
        cat.setPersonality(request.getPersonality());
        cat.setReasonForAdoption(request.getReasonForAdoption());
        cat.setContactMethod(request.getContactMethod());
        cat.setHealthStatus(request.getHealthStatus());
        cat.setStatus("available");
        adoptionCatMapper.insert(cat);
        return cat;
    }

    @Override
    public AdoptionApplication apply(AdoptionApplyRequest request, Long userId) {
        // 检查猫咪是否可领养
        AdoptionCat cat = adoptionCatMapper.selectById(request.getAdoptId());
        if (cat == null || !"available".equals(cat.getStatus())) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }

        // 检查是否重复申请
        Long existing = applicationMapper.selectCount(
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getAdoptId, request.getAdoptId())
                        .eq(AdoptionApplication::getApplicantUserId, userId)
                        .in(AdoptionApplication::getStatus, "pending", "approved"));
        if (existing > 0) {
            throw new BizException(400, "您已提交过该猫咪的领养申请");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setAdoptId(request.getAdoptId());
        application.setApplicantUserId(userId);
        application.setSelfIntro(request.getSelfIntro());
        application.setExperience(request.getExperience());
        application.setFamilyEnv(request.getFamilyEnv());
        application.setCommitmentSigned(request.getCommitmentSigned());
        application.setStatus("pending");
        applicationMapper.insert(application);

        return application;
    }

    @Override
    public List<AdoptionApplication> myApplications(Long userId) {
        return applicationMapper.selectList(
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getApplicantUserId, userId)
                        .orderByDesc(AdoptionApplication::getCreatedAt));
    }
}
