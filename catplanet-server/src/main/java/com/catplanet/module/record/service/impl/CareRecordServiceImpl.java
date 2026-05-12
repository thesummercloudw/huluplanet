package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.CareRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.CareRecord;
import com.catplanet.module.record.mapper.CareRecordMapper;
import com.catplanet.module.record.service.CareRecordService;
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

    @Override
    public RecordStatsResponse getStats(Long familyId, int days) {
        LocalDateTime startTime = LocalDate.now().minusDays(days - 1).atStartOfDay();
        List<CareRecord> records = careRecordMapper.selectList(
                new LambdaQueryWrapper<CareRecord>()
                        .eq(CareRecord::getFamilyId, familyId)
                        .ge(CareRecord::getDoneAt, startTime)
                        .orderByAsc(CareRecord::getDoneAt));

        // 每日统计
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<CareRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> r.getDoneAt().toLocalDate().format(fmt)));

        List<RecordStatsResponse.DailyStat> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(fmt);
            List<CareRecord> dayRecords = grouped.getOrDefault(dateStr, Collections.emptyList());
            dailyStats.add(new RecordStatsResponse.DailyStat(dateStr, dayRecords.size(), BigDecimal.valueOf(dayRecords.size())));
        }

        // 类型分布（按careType）
        Map<String, Integer> typeDistribution = new LinkedHashMap<>();
        records.forEach(r -> typeDistribution.merge(
                r.getCareType() != null ? r.getCareType() : "other", 1, Integer::sum));

        RecordStatsResponse response = new RecordStatsResponse();
        response.setDailyStats(dailyStats);
        response.setTypeDistribution(typeDistribution);
        response.setTotalCount(records.size());
        response.setTotalValue(BigDecimal.valueOf(records.size()));
        return response;
    }
}
