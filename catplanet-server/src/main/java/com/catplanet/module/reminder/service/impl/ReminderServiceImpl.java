package com.catplanet.module.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.reminder.dto.ReminderRequest;
import com.catplanet.module.reminder.entity.Reminder;
import com.catplanet.module.reminder.mapper.ReminderMapper;
import com.catplanet.module.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderMapper reminderMapper;

    @Override
    public Reminder create(ReminderRequest request, Long familyId, Long userId) {
        Reminder reminder = new Reminder();
        reminder.setFamilyId(familyId);
        reminder.setCatId(request.getCatId());
        reminder.setType(request.getType());
        reminder.setTitle(request.getTitle());
        reminder.setTriggerAt(request.getTriggerAt());
        reminder.setRepeatRule(request.getRepeatRule());
        reminder.setSourceRecordId(request.getSourceRecordId());
        reminder.setStatus("pending");
        reminder.setSubscribedUserIds(List.of(userId));
        reminderMapper.insert(reminder);
        return reminder;
    }

    @Override
    public List<Reminder> listByFamily(Long familyId, String status) {
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<Reminder>()
                .eq(Reminder::getFamilyId, familyId)
                .orderByAsc(Reminder::getTriggerAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Reminder::getStatus, status);
        }
        return reminderMapper.selectList(wrapper);
    }

    @Override
    public List<Reminder> listByCat(Long catId, Long familyId) {
        return reminderMapper.selectList(
                new LambdaQueryWrapper<Reminder>()
                        .eq(Reminder::getCatId, catId)
                        .eq(Reminder::getFamilyId, familyId)
                        .ne(Reminder::getStatus, "cancelled")
                        .orderByAsc(Reminder::getTriggerAt));
    }

    @Override
    public Reminder markDone(Long reminderId, Long familyId) {
        Reminder reminder = getAndVerify(reminderId, familyId);
        reminder.setStatus("done");
        reminderMapper.updateById(reminder);
        return reminder;
    }

    @Override
    public Reminder cancel(Long reminderId, Long familyId) {
        Reminder reminder = getAndVerify(reminderId, familyId);
        reminder.setStatus("cancelled");
        reminderMapper.updateById(reminder);
        return reminder;
    }

    @Override
    public void delete(Long reminderId, Long familyId) {
        Reminder reminder = getAndVerify(reminderId, familyId);
        reminderMapper.deleteById(reminder.getReminderId());
    }

    @Override
    public List<Reminder> listDueReminders() {
        return reminderMapper.selectList(
                new LambdaQueryWrapper<Reminder>()
                        .eq(Reminder::getStatus, "pending")
                        .le(Reminder::getTriggerAt, LocalDateTime.now()));
    }

    @Override
    public void markSent(Long reminderId) {
        reminderMapper.update(null,
                new LambdaUpdateWrapper<Reminder>()
                        .eq(Reminder::getReminderId, reminderId)
                        .set(Reminder::getStatus, "sent"));
    }

    private Reminder getAndVerify(Long reminderId, Long familyId) {
        Reminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null || !reminder.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        return reminder;
    }
}
