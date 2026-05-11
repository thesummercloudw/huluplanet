package com.catplanet.module.catfood.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UgcReviewRequest {

    @NotNull(message = "请选择猫粮")
    private Long foodId;

    private Long catId;

    @NotNull(message = "请评分")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer score;

    @NotBlank(message = "请输入短评内容")
    private String content;

    private List<String> images;
}
