package com.catplanet.module.adoption.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdoptionApplyRequest {

    @NotNull(message = "请选择要领养的猫咪")
    private Long adoptId;

    private String selfIntro;
    private String experience;
    private String familyEnv;

    @NotNull(message = "请签署养宠承诺")
    private Integer commitmentSigned;
}
