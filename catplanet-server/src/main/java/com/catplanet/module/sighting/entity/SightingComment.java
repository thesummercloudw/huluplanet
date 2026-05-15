package com.catplanet.module.sighting.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.catplanet.common.serializer.ImageUrlListSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "sighting_comment", autoResultMap = true)
public class SightingComment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long commentId;

    private Long sightingId;
    private Long userId;
    private String content;

    @JsonSerialize(using = ImageUrlListSerializer.class)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
