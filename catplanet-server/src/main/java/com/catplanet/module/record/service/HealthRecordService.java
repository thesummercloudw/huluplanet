package com.catplanet.module.record.service;

import com.catplanet.module.record.dto.HealthRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.HealthRecord;

import java.util.List;

public interface HealthRecordService {

    HealthRecord create(HealthRecordRequest request, Long familyId, Long userId);

    List<HealthRecord> listByCat(Long catId, Long familyId);

    List<HealthRecord> listByFamily(Long familyId, Long catId, int limit);

    void delete(Long recordId, Long familyId);

    RecordStatsResponse getStats(Long familyId, Long catId, int days);
}
