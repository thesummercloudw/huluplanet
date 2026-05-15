package com.catplanet.module.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 20, message = "昵称不能超过20个字符")
    private String nickname;

    private String avatar;
}
