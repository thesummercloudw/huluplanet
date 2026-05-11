package com.catplanet.module.family.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_member")
public class FamilyMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long familyId;
    private Long userId;
    private String role;
    private String nicknameInFamily;
    private LocalDateTime joinedAt;
}
