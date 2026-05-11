package com.catplanet.module.record.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.CareRecordRequest;
import com.catplanet.module.record.entity.CareRecord;
import com.catplanet.module.record.service.CareRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records/care")
@RequiredArgsConstructor
public class CareRecordController {

    private final CareRecordService careRecordService;

    @PostMapping
    public Result<CareRecord> create(@Valid @RequestBody CareRecordRequest request) {
        Long familyId = requireFamilyId();
        CareRecord record = careRecordService.create(request, familyId, UserContext.getUserId());
        return Result.ok(record);
    }

    @GetMapping("/cat/{catId}")
    public Result<List<CareRecord>> listByCat(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        return Result.ok(careRecordService.listByCat(catId, familyId));
    }

    @GetMapping
    public Result<List<CareRecord>> listByFamily(@RequestParam(defaultValue = "20") int limit) {
        Long familyId = requireFamilyId();
        return Result.ok(careRecordService.listByFamily(familyId, limit));
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> delete(@PathVariable Long recordId) {
        Long familyId = requireFamilyId();
        careRecordService.delete(recordId, familyId);
        return Result.ok();
    }

    private Long requireFamilyId() {
        Long familyId = UserContext.getFamilyId();
        if (familyId == null) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "请先创建或加入一个家庭");
        }
        return familyId;
    }
}
