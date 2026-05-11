package com.catplanet.module.reminder.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.reminder.dto.ReminderRequest;
import com.catplanet.module.reminder.entity.Reminder;
import com.catplanet.module.reminder.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public Result<Reminder> create(@Valid @RequestBody ReminderRequest request) {
        Long familyId = requireFamilyId();
        Reminder reminder = reminderService.create(request, familyId, UserContext.getUserId());
        return Result.ok(reminder);
    }

    @GetMapping
    public Result<List<Reminder>> list(@RequestParam(required = false) String status) {
        Long familyId = requireFamilyId();
        return Result.ok(reminderService.listByFamily(familyId, status));
    }

    @GetMapping("/cat/{catId}")
    public Result<List<Reminder>> listByCat(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        return Result.ok(reminderService.listByCat(catId, familyId));
    }

    @PutMapping("/{reminderId}/done")
    public Result<Reminder> markDone(@PathVariable Long reminderId) {
        Long familyId = requireFamilyId();
        return Result.ok(reminderService.markDone(reminderId, familyId));
    }

    @PutMapping("/{reminderId}/cancel")
    public Result<Reminder> cancel(@PathVariable Long reminderId) {
        Long familyId = requireFamilyId();
        return Result.ok(reminderService.cancel(reminderId, familyId));
    }

    @DeleteMapping("/{reminderId}")
    public Result<Void> delete(@PathVariable Long reminderId) {
        Long familyId = requireFamilyId();
        reminderService.delete(reminderId, familyId);
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
