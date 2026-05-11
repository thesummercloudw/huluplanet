package com.catplanet.module.family.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family")
public class Family {

    @TableId(type = IdType.ASSIGN_ID)
    private Long familyId;

    private String name;
    private String coverEmoji;
    private Long creatorId;
    private String inviteCode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
