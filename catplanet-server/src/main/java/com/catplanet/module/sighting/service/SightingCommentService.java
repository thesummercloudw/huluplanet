package com.catplanet.module.sighting.service;

import com.catplanet.module.sighting.dto.SightingCommentRequest;
import com.catplanet.module.sighting.entity.SightingComment;
import com.catplanet.module.sighting.vo.SightingCommentVO;

import java.util.List;

public interface SightingCommentService {

    SightingComment create(SightingCommentRequest request, Long userId);

    List<SightingCommentVO> listBySighting(Long sightingId, int page, int size);

    void delete(Long commentId, Long userId);
}
