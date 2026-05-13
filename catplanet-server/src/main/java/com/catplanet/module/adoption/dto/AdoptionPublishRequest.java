package com.catplanet.module.adoption.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdoptionPublishRequest {

    @NotBlank(message = "猫咪名字不能为空")
    private String name;

    private String cover;
    private List<String> images;
    private String gender;          // male/female/unknown
    private String ageEstimate;
    private String breedEstimate;

    @NotBlank(message = "请填写所在城市")
    private String city;

    private String personality;
    private String reasonForAdoption;

    @NotBlank(message = "请填写联系方式")
    private String contactMethod;

    private Map<String, Object> healthStatus;
}
