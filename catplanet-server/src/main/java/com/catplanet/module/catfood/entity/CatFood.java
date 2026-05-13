package com.catplanet.module.catfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "cat_food", autoResultMap = true)
public class CatFood {

    @TableId(type = IdType.ASSIGN_ID)
    private Long foodId;

    private String brand;
    private String name;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String image;
    private String ageStage;    // kitten/adult/senior/all
    private String foodType;    // main/wet/snack/freeze_dried
    private String priceRange;
    private BigDecimal proteinPct;
    private BigDecimal fatPct;
    private String ingredientsSummary;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private BigDecimal avgScore;
    private Integer reviewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
