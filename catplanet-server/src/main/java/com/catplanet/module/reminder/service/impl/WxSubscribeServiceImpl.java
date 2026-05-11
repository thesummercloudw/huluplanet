package com.catplanet.module.reminder.service.impl;

import com.catplanet.module.reminder.entity.Reminder;
import com.catplanet.module.reminder.service.WxSubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 微信订阅消息推送实现
 * dev 模式下仅打印日志，生产环境调用微信 API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxSubscribeServiceImpl implements WxSubscribeService {

    @Value("${catplanet.dev-mode:false}")
    private boolean devMode;

    @Value("${catplanet.wx.appid:}")
    private String appId;

    @Value("${catplanet.wx.secret:}")
    private String appSecret;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    @Override
    public void sendReminderNotification(Reminder reminder, String catName) {
        if (reminder.getSubscribedUserIds() == null || reminder.getSubscribedUserIds().isEmpty()) {
            log.info("[提醒] reminder={} 无订阅用户，跳过", reminder.getReminderId());
            return;
        }

        String timeStr = reminder.getTriggerAt().format(FMT);

        if (devMode) {
            // Dev 模式：仅日志输出
            log.info("[提醒·DEV] 模拟推送 → 提醒ID={}, 猫咪={}, 标题={}, 时间={}, 订阅用户={}",
                    reminder.getReminderId(), catName, reminder.getTitle(), timeStr,
                    reminder.getSubscribedUserIds());
            return;
        }

        // 生产模式：调用微信订阅消息 API
        // TODO: 实现真实微信 subscribeMessage.send 调用
        // 1. 获取 access_token（从缓存或微信 API）
        // 2. 遍历 subscribedUserIds，通过 openid 发送模板消息
        // 模板示例：
        //   thing1: 猫咪名称 (catName)
        //   thing2: 提醒标题 (reminder.getTitle())
        //   time3: 时间 (timeStr)
        log.info("[提醒·PROD] 发送微信订阅消息 → 提醒ID={}, 猫咪={}, 标题={}",
                reminder.getReminderId(), catName, reminder.getTitle());
    }
}
