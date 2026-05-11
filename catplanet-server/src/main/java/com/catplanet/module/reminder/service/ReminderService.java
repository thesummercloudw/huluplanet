package com.catplanet.module.reminder.service;

import com.catplanet.module.reminder.dto.ReminderRequest;
import com.catplanet.module.reminder.entity.Reminder;

import java.util.List;

public interface ReminderService {

    Reminder create(ReminderRequest request, Long familyId, Long userId);

    List<Reminder> listByFamily(Long familyId, String status);

    List<Reminder> listByCat(Long catId, Long familyId);

    Reminder markDone(Long reminderId, Long familyId);

    Reminder cancel(Long reminderId, Long familyId);

    void delete(Long reminderId, Long familyId);

    /**
     * 查询所有到期待发送的提醒
     */
    List<Reminder> listDueReminders();

    /**
     * 标记提醒为已发送
     */
    void markSent(Long reminderId);
}
