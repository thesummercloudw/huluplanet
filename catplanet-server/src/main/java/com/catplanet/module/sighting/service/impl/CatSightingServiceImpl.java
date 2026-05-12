package com.catplanet.module.sighting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.module.sighting.dto.SightingRequest;
import com.catplanet.module.sighting.entity.CatSighting;
import com.catplanet.module.sighting.mapper.CatSightingMapper;
import com.catplanet.module.sighting.service.CatSightingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatSightingServiceImpl implements CatSightingService {

    private final CatSightingMapper sightingMapper;

    @Override
    public CatSighting create(SightingRequest request, Long userId) {
        CatSighting sighting = new CatSighting();
        sighting.setUserId(userId);
        sighting.setImage(request.getImage());
        sighting.setContent(request.getContent());
        sighting.setLat(request.getLat());
        sighting.setLng(request.getLng());
        sighting.setAddress(request.getAddress());
        sighting.setLikeCount(0);
        sightingMapper.insert(sighting);
        return sighting;
    }

    @Override
    public List<CatSighting> listRecent(int limit) {
        return sightingMapper.selectList(
                new LambdaQueryWrapper<CatSighting>()
                        .orderByDesc(CatSighting::getCreatedAt)
                        .last("LIMIT " + limit)
        );
    }

    @Override
    public void delete(Long sightingId, Long userId) {
        CatSighting sighting = sightingMapper.selectById(sightingId);
        if (sighting == null || !sighting.getUserId().equals(userId)) {
            throw new BizException(404, "记录不存在");
        }
        sightingMapper.deleteById(sightingId);
    }
}
