package com.catplanet.module.record.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.HealthRecordRequest;
import com.catplanet.module.record.dto.RecordStatsResponse;
import com.catplanet.module.record.entity.HealthRecord;
import com.catplanet.module.record.service.HealthRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @PostMapping
    public Result<HealthRecord> create(@Valid @RequestBody HealthRecordRequest request) {
        Long familyId = requireFamilyId();
        HealthRecord record = healthRecordService.create(request, familyId, UserContext.getUserId());
        return Result.ok(record);
    }

    @GetMapping("/cat/{catId}")
    public Result<List<HealthRecord>> listByCat(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        return Result.ok(healthRecordService.listByCat(catId, familyId));
    }

    @GetMapping
    public Result<List<HealthRecord>> listByFamily(@RequestParam(defaultValue = "20") int limit) {
        Long familyId = requireFamilyId();
        return Result.ok(healthRecordService.listByFamily(familyId, limit));
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> delete(@PathVariable Long recordId) {
        Long familyId = requireFamilyId();
        healthRecordService.delete(recordId, familyId);
        return Result.ok();
    }

    @GetMapping("/stats")
    public Result<RecordStatsResponse> getStats(@RequestParam(defaultValue = "7") int days) {
        Long familyId = requireFamilyId();
        return Result.ok(healthRecordService.getStats(familyId, days));
    }

    private Long requireFamilyId() {
        Long familyId = UserContext.getFamilyId();
        if (familyId == null) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "请先创建或加入一个家庭");
        }
        return familyId;
    }
}
