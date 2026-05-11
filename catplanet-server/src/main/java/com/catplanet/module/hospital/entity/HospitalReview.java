package com.catplanet.module.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "hospital_review", autoResultMap = true)
public class HospitalReview {

    @TableId(type = IdType.ASSIGN_ID)
    private Long reviewId;

    private Long hospitalId;
    private Long userId;
    private Integer score;
    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> serviceTags;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private String auditStatus;  // pending/approved/rejected

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
