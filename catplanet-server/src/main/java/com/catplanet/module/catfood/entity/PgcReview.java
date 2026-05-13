package com.catplanet.module.catfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pgc_review")
public class PgcReview {

    @TableId(type = IdType.ASSIGN_ID)
    private Long reviewId;

    private Long foodId;
    private Long authorId;
    private String title;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String cover;
    private String contentMd;
    private Integer scoreIngredient;
    private Integer scoreNutrition;
    private Integer scoreValue;
    private Integer scorePalatability;
    private Integer scoreSafety;
    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
