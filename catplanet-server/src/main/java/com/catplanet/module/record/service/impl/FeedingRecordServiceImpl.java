package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.FeedingRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.FeedingRecord;
import com.catplanet.module.record.mapper.FeedingRecordMapper;
import com.catplanet.module.record.service.FeedingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public RecordStatsResponse getStats(Long familyId, int days) {
        LocalDateTime startTime = LocalDate.now().minusDays(days - 1).atStartOfDay();
        List<FeedingRecord> records = feedingRecordMapper.selectList(
                new LambdaQueryWrapper<FeedingRecord>()
                        .eq(FeedingRecord::getFamilyId, familyId)
                        .ge(FeedingRecord::getFedAt, startTime)
                        .orderByAsc(FeedingRecord::getFedAt));

        // 每日统计
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<FeedingRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> r.getFedAt().toLocalDate().format(fmt)));

        List<RecordStatsResponse.DailyStat> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(fmt);
            List<FeedingRecord> dayRecords = grouped.getOrDefault(dateStr, Collections.emptyList());
            int totalG = dayRecords.stream()
                    .mapToInt(r -> r.getAmountG() != null ? r.getAmountG() : 0).sum();
            dailyStats.add(new RecordStatsResponse.DailyStat(dateStr, dayRecords.size(), BigDecimal.valueOf(totalG)));
        }

        // 类型分布（按mealType）
        Map<String, Integer> typeDistribution = new LinkedHashMap<>();
        records.forEach(r -> typeDistribution.merge(
                r.getMealType() != null ? r.getMealType() : "main", 1, Integer::sum));

        // 汇总
        int totalAmount = records.stream()
                .mapToInt(r -> r.getAmountG() != null ? r.getAmountG() : 0).sum();

        RecordStatsResponse response = new RecordStatsResponse();
        response.setDailyStats(dailyStats);
        response.setTypeDistribution(typeDistribution);
        response.setTotalCount(records.size());
        response.setTotalValue(BigDecimal.valueOf(totalAmount));
        return response;
    }
}
