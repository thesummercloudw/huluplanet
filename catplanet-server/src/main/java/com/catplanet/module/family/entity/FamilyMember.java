package com.catplanet.module.family.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_member")
public class FamilyMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long familyId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long userId;

    private String role;
    private String nicknameInFamily;
    private LocalDateTime joinedAt;
}
