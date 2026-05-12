package com.catplanet.module.sighting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SightingRequest {

    @NotBlank(message = "图片不能为空")
    private String image;

    private String content;

    @NotNull(message = "定位纬度不能为空")
    private BigDecimal lat;

    @NotNull(message = "定位经度不能为空")
    private BigDecimal lng;

    private String address;
}
