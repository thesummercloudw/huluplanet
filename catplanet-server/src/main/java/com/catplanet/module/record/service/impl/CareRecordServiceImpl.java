package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.CareRecordRequest;
import com.catplanet.module.record.entity.CareRecord;
import com.catplanet.module.record.mapper.CareRecordMapper;
import com.catplanet.module.record.service.CareRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareRecordServiceImpl implements CareRecordService {

    private final CareRecordMapper careRecordMapper;

    @Override
    public CareRecord create(CareRecordRequest request, Long familyId, Long userId) {
        CareRecord record = new CareRecord();
        record.setCatId(request.getCatId());
        record.setFamilyId(familyId);
        record.setCareType(request.getCareType());
        record.setDoneAt(request.getDoneAt() != null ? request.getDoneAt() : LocalDateTime.now());
        record.setOperatorUserId(userId);
        record.setNote(request.getNote());
        record.setImages(request.getImages());
        careRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<CareRecord> listByCat(Long catId, Long familyId) {
        return careRecordMapper.selectList(
                new LambdaQueryWrapper<CareRecord>()
                        .eq(CareRecord::getCatId, catId)
                        .eq(CareRecord::getFamilyId, familyId)
                        .orderByDesc(CareRecord::getDoneAt));
    }

    @Override
    public List<CareRecord> listByFamily(Long familyId, int limit) {
        return careRecordMapper.selectList(
                new LambdaQueryWrapper<CareRecord>()
                        .eq(CareRecord::getFamilyId, familyId)
                        .orderByDesc(CareRecord::getDoneAt)
                        .last("LIMIT " + limit));
    }

    @Override
    public void delete(Long recordId, Long familyId) {
        CareRecord record = careRecordMapper.selectById(recordId);
        if (record == null || !record.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        careRecordMapper.deleteById(recordId);
    }
}
