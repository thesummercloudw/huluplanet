package com.catplanet.module.sighting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cat_sighting")
public class CatSighting {

    @TableId(type = IdType.ASSIGN_ID)
    private Long sightingId;

    private Long userId;
    private String image;
    private String content;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private Integer likeCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
