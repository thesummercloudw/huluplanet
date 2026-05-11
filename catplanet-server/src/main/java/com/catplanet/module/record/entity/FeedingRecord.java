package com.catplanet.module.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feeding_record")
public class FeedingRecord {

    @TableId(type = IdType.ASSIGN_ID)
    private Long recordId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long catId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long familyId;

    private String foodName;
    private Integer amountG;
    private String mealType;
    private LocalDateTime fedAt;
    private Long operatorUserId;
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
