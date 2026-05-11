package com.catplanet.module.record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class HealthRecordRequest {

    @NotNull(message = "请选择猫咪")
    private Long catId;

    @NotBlank(message = "请选择健康类型")
    private String healthType;

    private String subtype;

    @NotNull(message = "请选择日期")
    private LocalDate recordDate;

    private String hospitalName;

    private BigDecimal cost;

    private LocalDate nextDueDate;

    private BigDecimal valueNumeric;

    private String note;

    private List<String> images;
}
