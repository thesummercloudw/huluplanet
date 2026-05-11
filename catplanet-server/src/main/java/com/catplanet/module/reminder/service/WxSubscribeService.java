package com.catplanet.module.reminder.service;

import com.catplanet.module.reminder.entity.Reminder;

/**
 * 微信订阅消息推送服务
 */
public interface WxSubscribeService {

    /**
     * 发送提醒通知给订阅用户
     *
     * @param reminder 提醒实体
     * @param catName  猫咪名称（用于消息模板）
     */
    void sendReminderNotification(Reminder reminder, String catName);
}
