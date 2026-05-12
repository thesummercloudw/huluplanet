package com.catplanet.module.record.service;

import com.catplanet.module.record.dto.CareRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.CareRecord;

import java.util.List;

public interface CareRecordService {

    CareRecord create(CareRecordRequest request, Long familyId, Long userId);

    List<CareRecord> listByCat(Long catId, Long familyId);

    List<CareRecord> listByFamily(Long familyId, int limit);

    void delete(Long recordId, Long familyId);

    RecordStatsResponse getStats(Long familyId, int days);
}
