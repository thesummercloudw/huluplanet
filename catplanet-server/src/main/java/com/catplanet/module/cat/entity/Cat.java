package com.catplanet.module.cat.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "cat", autoResultMap = true)
public class Cat {

    @TableId(type = IdType.ASSIGN_ID)
    private Long catId;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long familyId;
    private String name;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> personalityTags;

    private LocalDate adoptionDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
