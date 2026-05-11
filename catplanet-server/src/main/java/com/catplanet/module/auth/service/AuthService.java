package com.catplanet.module.auth.service;

import com.catplanet.module.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse wxLogin(String code);
}
