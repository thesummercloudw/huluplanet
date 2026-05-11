package com.catplanet.module.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "adoption_cat", autoResultMap = true)
public class AdoptionCat {

    @TableId(type = IdType.ASSIGN_ID)
    private Long adoptId;

    private String name;
    private String cover;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private String gender;          // male/female/unknown
    private String ageEstimate;
    private String breedEstimate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> healthStatus;

    private String personality;
    private String city;
    private String reasonForAdoption;
    private String contactMethod;   // 仅运营内部可见
    private String status;          // available/pending/adopted

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
