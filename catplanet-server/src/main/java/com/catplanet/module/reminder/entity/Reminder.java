package com.catplanet.module.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "reminder", autoResultMap = true)
public class Reminder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long reminderId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long familyId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long catId;

    private Long sourceRecordId;

    private String type; // vaccine / deworm / checkup / feeding / care / custom

    private String title;

    private LocalDateTime triggerAt;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> repeatRule;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> subscribedUserIds;

    private String status; // pending / sent / done / cancelled

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
