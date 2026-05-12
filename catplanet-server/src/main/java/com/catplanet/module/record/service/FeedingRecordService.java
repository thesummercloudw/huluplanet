package com.catplanet.module.record.service;

import com.catplanet.module.record.dto.FeedingRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.FeedingRecord;

import java.util.List;

public interface FeedingRecordService {

    FeedingRecord create(FeedingRecordRequest request, Long familyId, Long userId);

    List<FeedingRecord> listByCat(Long catId, Long familyId);

    List<FeedingRecord> listByFamily(Long familyId, int limit);

    void delete(Long recordId, Long familyId);

    RecordStatsResponse getStats(Long familyId, int days);
}
