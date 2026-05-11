package com.catplanet.module.hospital.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class HospitalReviewRequest {

    @NotNull(message = "请选择医院")
    private Long hospitalId;

    @NotNull(message = "请评分")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer score;

    private String content;
    private List<String> serviceTags;
    private List<String> images;
}
