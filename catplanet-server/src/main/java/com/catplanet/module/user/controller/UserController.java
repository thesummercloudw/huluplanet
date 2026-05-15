package com.catplanet.module.user.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.user.dto.UpdateProfileRequest;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        return Result.ok(user);
    }

    /**
     * 更新用户头像和昵称
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = UserContext.getUserId();
        User user = userService.updateProfile(userId, request);
        return Result.ok(user);
    }
}
