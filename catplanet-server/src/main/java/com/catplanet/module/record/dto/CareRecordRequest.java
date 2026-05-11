package com.catplanet.module.record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CareRecordRequest {

    @NotNull(message = "请选择猫咪")
    private Long catId;

    @NotBlank(message = "请选择养护类型")
    private String careType;

    private LocalDateTime doneAt;

    private String note;

    private List<String> images;
}
