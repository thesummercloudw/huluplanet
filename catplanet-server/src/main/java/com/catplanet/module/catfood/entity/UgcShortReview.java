package com.catplanet.module.catfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "ugc_short_review", autoResultMap = true)
public class UgcShortReview {

    @TableId(type = IdType.ASSIGN_ID)
    private Long reviewId;

    private Long foodId;
    private Long userId;
    private Long catId;
    private Integer score;      // 1-5
    private String content;     // ≤200字

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private String auditStatus; // pending/approved/rejected
    private String auditNote;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
