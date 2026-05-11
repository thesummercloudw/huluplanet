package com.catplanet.module.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adoption_application")
public class AdoptionApplication {

    @TableId(type = IdType.ASSIGN_ID)
    private Long applyId;

    private Long adoptId;
    private Long applicantUserId;
    private String selfIntro;
    private String experience;
    private String familyEnv;
    private Integer commitmentSigned;   // 0/1
    private String status;              // pending/approved/rejected/cancelled
    private String operatorNote;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
