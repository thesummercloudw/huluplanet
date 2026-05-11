package com.catplanet.module.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "health_record", autoResultMap = true)
public class HealthRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long recordId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long catId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long familyId;

    private String healthType;
    private String subtype;
    private LocalDate recordDate;
    private String hospitalName;
    private BigDecimal cost;
    private LocalDate nextDueDate;
    private BigDecimal valueNumeric;
    private Long operatorUserId;
    private String note;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
