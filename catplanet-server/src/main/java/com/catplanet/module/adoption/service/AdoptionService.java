package com.catplanet.module.adoption.service;

import com.catplanet.module.adoption.dto.AdoptionApplyRequest;
import com.catplanet.module.adoption.dto.AdoptionPublishRequest;
import com.catplanet.module.adoption.entity.AdoptionApplication;
import com.catplanet.module.adoption.entity.AdoptionCat;

import java.util.List;

public interface AdoptionService {

    List<AdoptionCat> listAvailable(String city, int page, int size);

    AdoptionCat getById(Long adoptId);

    AdoptionCat publish(AdoptionPublishRequest request, Long userId);

    AdoptionApplication apply(AdoptionApplyRequest request, Long userId);

    List<AdoptionApplication> myApplications(Long userId);
}
