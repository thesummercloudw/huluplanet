package com.catplanet.module.sighting.service;

import com.catplanet.module.sighting.dto.SightingRequest;
import com.catplanet.module.sighting.entity.CatSighting;

import java.util.List;

public interface CatSightingService {

    CatSighting create(SightingRequest request, Long userId);

    List<CatSighting> listRecent(int limit);

    void delete(Long sightingId, Long userId);
}
