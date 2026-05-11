package com.catplanet.module.record.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.record.dto.FeedingRecordRequest;
import com.catplanet.module.record.entity.FeedingRecord;
import com.catplanet.module.record.service.FeedingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records/feeding")
@RequiredArgsConstructor
public class FeedingRecordController {

    private final FeedingRecordService feedingRecordService;

    @PostMapping
    public Result<FeedingRecord> create(@Valid @RequestBody FeedingRecordRequest request) {
        Long familyId = requireFamilyId();
        FeedingRecord record = feedingRecordService.create(request, familyId, UserContext.getUserId());
        return Result.ok(record);
    }

    @GetMapping("/cat/{catId}")
    public Result<List<FeedingRecord>> listByCat(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        return Result.ok(feedingRecordService.listByCat(catId, familyId));
    }

    @GetMapping
    public Result<List<FeedingRecord>> listByFamily(@RequestParam(defaultValue = "20") int limit) {
        Long familyId = requireFamilyId();
        return Result.ok(feedingRecordService.listByFamily(familyId, limit));
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> delete(@PathVariable Long recordId) {
        Long familyId = requireFamilyId();
        feedingRecordService.delete(recordId, familyId);
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
