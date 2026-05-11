package com.catplanet.module.cat.service;

import com.catplanet.module.cat.dto.CatRequest;
import com.catplanet.module.cat.entity.Cat;

import java.util.List;

public interface CatService {

    Cat create(CatRequest request, Long familyId);

    Cat update(Long catId, CatRequest request, Long familyId);

    void delete(Long catId, Long familyId);

    Cat getById(Long catId, Long familyId);

    List<Cat> listByFamilyId(Long familyId);
}
