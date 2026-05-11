package com.catplanet.module.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ReminderRequest {

    @NotNull(message = "请选择猫咪")
    private Long catId;

    @NotBlank(message = "请选择提醒类型")
    private String type;

    @NotBlank(message = "请输入提醒标题")
    private String title;

    @NotNull(message = "请设置提醒时间")
    private LocalDateTime triggerAt;

    private Map<String, Object> repeatRule;

    private Long sourceRecordId;
}
