package com.catplanet.module.auth.controller;

import com.catplanet.common.result.Result;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.dto.WxLoginRequest;
import com.catplanet.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/wx-login")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        LoginResponse response = authService.wxLogin(request.getCode());
        return Result.ok(response);
    }
}
