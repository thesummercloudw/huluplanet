package com.catplanet.module.family.service;

import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.UpdateFamilyRequest;
import com.catplanet.module.family.entity.Family;

import java.util.List;

public interface FamilyService {

    Family create(CreateFamilyRequest request, Long userId);

    Family joinByInviteCode(String inviteCode, Long userId);

    void leave(Long familyId, Long userId);

    List<Family> listByUserId(Long userId);

    Family updateName(Long familyId, UpdateFamilyRequest request, Long userId);
}
