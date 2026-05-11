package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.FeedingRecordRequest;
import com.catplanet.module.record.entity.FeedingRecord;
import com.catplanet.module.record.mapper.FeedingRecordMapper;
import com.catplanet.module.record.service.FeedingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedingRecordServiceImpl implements FeedingRecordService {

    private final FeedingRecordMapper feedingRecordMapper;

    @Override
    public FeedingRecord create(FeedingRecordRequest request, Long familyId, Long userId) {
        FeedingRecord record = new FeedingRecord();
        record.setCatId(request.getCatId());
        record.setFamilyId(familyId);
        record.setFoodName(request.getFoodName());
        record.setAmountG(request.getAmountG());
        record.setMealType(request.getMealType() != null ? request.getMealType() : "main");
        record.setFedAt(request.getFedAt() != null ? request.getFedAt() : LocalDateTime.now());
        record.setOperatorUserId(userId);
        record.setNote(request.getNote());
        feedingRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<FeedingRecord> listByCat(Long catId, Long familyId) {
        return feedingRecordMapper.selectList(
                new LambdaQueryWrapper<FeedingRecord>()
                        .eq(FeedingRecord::getCatId, catId)
                        .eq(FeedingRecord::getFamilyId, familyId)
                        .orderByDesc(FeedingRecord::getFedAt));
    }

    @Override
    public List<FeedingRecord> listByFamily(Long familyId, int limit) {
        return feedingRecordMapper.selectList(
                new LambdaQueryWrapper<FeedingRecord>()
                        .eq(FeedingRecord::getFamilyId, familyId)
                        .orderByDesc(FeedingRecord::getFedAt)
                        .last("LIMIT " + limit));
    }

    @Override
    public void delete(Long recordId, Long familyId) {
        FeedingRecord record = feedingRecordMapper.selectById(recordId);
        if (record == null || !record.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        feedingRecordMapper.deleteById(recordId);
    }
}
