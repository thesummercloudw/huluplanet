package com.catplanet.module.auth.controller;

import com.catplanet.common.result.Result;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;
import com.catplanet.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${catplanet.dev-mode:false}")
    private boolean devMode;

    @PostMapping("/wx-login")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        LoginResponse response;
        if (devMode) {
            // 开发模式：跳过微信验证，直接用 mock openid 登录
            response = authService.devLogin(request.getCode());
        } else {
            response = authService.wxLogin(request.getCode());
        }
        return Result.ok(response);
    }
}
