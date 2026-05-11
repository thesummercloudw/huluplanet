package com.catplanet.module.cat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CatRequest {
    @NotBlank(message = "猫咪名字不能为空")
    private String name;
    private String avatar;
    private String breed;
    private String gender;
    private LocalDate birthday;
    private Integer isNeutered;
    private BigDecimal weightKg;
    private List<String> personalityTags;
    private LocalDate adoptionDate;
}
