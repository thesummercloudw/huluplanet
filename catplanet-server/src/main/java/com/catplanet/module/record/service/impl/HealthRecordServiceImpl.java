package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.HealthRecordRequest;
import com.catplanet.module.record.entity.HealthRecord;
import com.catplanet.module.record.mapper.HealthRecordMapper;
import com.catplanet.module.record.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;

    @Override
    public HealthRecord create(HealthRecordRequest request, Long familyId, Long userId) {
        HealthRecord record = new HealthRecord();
        record.setCatId(request.getCatId());
        record.setFamilyId(familyId);
        record.setHealthType(request.getHealthType());
        record.setSubtype(request.getSubtype());
        record.setRecordDate(request.getRecordDate());
        record.setHospitalName(request.getHospitalName());
        record.setCost(request.getCost());
        record.setNextDueDate(request.getNextDueDate());
        record.setValueNumeric(request.getValueNumeric());
        record.setOperatorUserId(userId);
        record.setNote(request.getNote());
        record.setImages(request.getImages());
        healthRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<HealthRecord> listByCat(Long catId, Long familyId) {
        return healthRecordMapper.selectList(
                new LambdaQueryWrapper<HealthRecord>()
                        .eq(HealthRecord::getCatId, catId)
                        .eq(HealthRecord::getFamilyId, familyId)
                        .orderByDesc(HealthRecord::getRecordDate));
    }

    @Override
    public List<HealthRecord> listByFamily(Long familyId, int limit) {
        return healthRecordMapper.selectList(
                new LambdaQueryWrapper<HealthRecord>()
                        .eq(HealthRecord::getFamilyId, familyId)
                        .orderByDesc(HealthRecord::getRecordDate)
                        .last("LIMIT " + limit));
    }

    @Override
    public void delete(Long recordId, Long familyId) {
        HealthRecord record = healthRecordMapper.selectById(recordId);
        if (record == null || !record.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        healthRecordMapper.deleteById(recordId);
    }
}
