package com.catplanet.module.reminder.scheduler;

import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.mapper.CatMapper;
import com.catplanet.module.reminder.entity.Reminder;
import com.catplanet.module.reminder.service.ReminderService;
import com.catplanet.module.reminder.service.WxSubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 提醒定时调度器
 * 每 5 分钟扫描到期提醒，触发推送
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderService reminderService;
    private final WxSubscribeService wxSubscribeService;
    private final CatMapper catMapper;

    @Scheduled(fixedRate = 300000) // 5 分钟
    public void scanDueReminders() {
        List<Reminder> dueReminders = reminderService.listDueReminders();
        if (dueReminders.isEmpty()) {
            return;
        }

        log.info("[提醒调度] 发现 {} 条到期提醒", dueReminders.size());

        for (Reminder reminder : dueReminders) {
            try {
                // 获取猫咪名称
                String catName = "猫咪";
                Cat cat = catMapper.selectById(reminder.getCatId());
                if (cat != null) {
                    catName = cat.getName();
                }

                // 推送通知
                wxSubscribeService.sendReminderNotification(reminder, catName);

                // 标记为已发送
                reminderService.markSent(reminder.getReminderId());

                log.info("[提醒调度] 已发送提醒: id={}, title={}", reminder.getReminderId(), reminder.getTitle());
            } catch (Exception e) {
                log.error("[提醒调度] 发送失败: id={}, error={}", reminder.getReminderId(), e.getMessage(), e);
            }
        }
    }
}
