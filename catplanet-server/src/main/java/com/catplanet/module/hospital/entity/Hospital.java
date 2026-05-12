package com.catplanet.module.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "hospital", autoResultMap = true)
public class Hospital {

    @TableId(type = IdType.ASSIGN_ID)
    private Long hospitalId;

    private String type;            // hospital=宠物医院, petstore=宠物店
    private String poiSource;       // tencent/amap/manual
    private String poiSourceId;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String phone;
    private String businessHours;

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
