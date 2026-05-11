package com.catplanet.module.record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedingRecordRequest {

    @NotNull(message = "请选择猫咪")
    private Long catId;

    @NotBlank(message = "请填写食物名称")
    private String foodName;

    private Integer amountG;

    private String mealType = "main";

    private LocalDateTime fedAt;

    private String note;
}
