package com.catplanet.module.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.HealthRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.HealthRecord;
import com.catplanet.module.record.mapper.HealthRecordMapper;
import com.catplanet.module.record.service.HealthRecordService;
import com.catplanet.module.reminder.dto.ReminderRequest;
import com.catplanet.module.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;
    private final ReminderService reminderService;

    /** 自动创建提醒的健康类型 */
    private static final Set<String> AUTO_REMINDER_TYPES = Set.of("vaccine", "deworm");

    private static final Map<String, String> TYPE_NAMES = Map.of(
            "vaccine", "疫苗",
            "deworm", "驱虫",
            "checkup", "体检"
    );

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

        // 自动创建提醒（疫苗/驱虫 且有下次到期日）
        if (AUTO_REMINDER_TYPES.contains(request.getHealthType()) && request.getNextDueDate() != null) {
            autoCreateReminder(record, request.getNextDueDate(), familyId, userId);
        }

        return record;
    }

    private void autoCreateReminder(HealthRecord record, LocalDate nextDueDate, Long familyId, Long userId) {
        String typeName = TYPE_NAMES.getOrDefault(record.getHealthType(), record.getHealthType());
        String subInfo = record.getSubtype() != null ? "·" + record.getSubtype() : "";

        // 提前 3 天提醒，时间设为上午 9:00
        LocalDateTime triggerAt = nextDueDate.minusDays(3).atTime(LocalTime.of(9, 0));
        if (triggerAt.isBefore(LocalDateTime.now())) {
            // 如果提前3天已经过了，就设为到期当天
            triggerAt = nextDueDate.atTime(LocalTime.of(9, 0));
        }

        ReminderRequest reminderReq = new ReminderRequest();
        reminderReq.setCatId(record.getCatId());
        reminderReq.setType(record.getHealthType());
        reminderReq.setTitle(typeName + subInfo + "到期提醒");
        reminderReq.setTriggerAt(triggerAt);
        reminderReq.setSourceRecordId(record.getRecordId());
        reminderService.create(reminderReq, familyId, userId);
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
    public List<HealthRecord> listByFamily(Long familyId, Long catId, int limit) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getFamilyId, familyId)
                .eq(catId != null, HealthRecord::getCatId, catId)
                .orderByDesc(HealthRecord::getRecordDate)
                .last("LIMIT " + limit);
        return healthRecordMapper.selectList(wrapper);
    }

    @Override
    public void delete(Long recordId, Long familyId) {
        HealthRecord record = healthRecordMapper.selectById(recordId);
        if (record == null || !record.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        healthRecordMapper.deleteById(recordId);
    }

    @Override
    public RecordStatsResponse getStats(Long familyId, Long catId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        List<HealthRecord> records = healthRecordMapper.selectList(
                new LambdaQueryWrapper<HealthRecord>()
                        .eq(HealthRecord::getFamilyId, familyId)
                        .eq(catId != null, HealthRecord::getCatId, catId)
                        .ge(HealthRecord::getRecordDate, startDate)
                        .orderByAsc(HealthRecord::getRecordDate));

        // 每日统计
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<HealthRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> r.getRecordDate().format(fmt)));

        List<RecordStatsResponse.DailyStat> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(fmt);
            List<HealthRecord> dayRecords = grouped.getOrDefault(dateStr, Collections.emptyList());
            BigDecimal dayCost = dayRecords.stream()
                    .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dailyStats.add(new RecordStatsResponse.DailyStat(dateStr, dayRecords.size(), dayCost));
        }

        // 类型分布（按healthType）
        Map<String, Integer> typeDistribution = new LinkedHashMap<>();
        records.forEach(r -> typeDistribution.merge(
                r.getHealthType() != null ? r.getHealthType() : "other", 1, Integer::sum));

        // 总花费
        BigDecimal totalCost = records.stream()
                .map(r -> r.getCost() != null ? r.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RecordStatsResponse response = new RecordStatsResponse();
        response.setDailyStats(dailyStats);
        response.setTypeDistribution(typeDistribution);
        response.setTotalCount(records.size());
        response.setTotalValue(totalCost);
        return response;
    }
}
