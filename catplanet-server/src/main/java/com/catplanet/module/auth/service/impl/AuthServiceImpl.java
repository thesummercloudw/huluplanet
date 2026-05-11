package com.catplanet.module.auth.service.impl;

import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.common.util.JwtUtil;
import com.catplanet.module.auth.dto.LoginResponse;
import com.catplanet.module.auth.service.AuthService;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WebClient webClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${catplanet.wx.appid}")
    private String appid;

    @Value("${catplanet.wx.secret}")
    private String secret;

    @Override
    public LoginResponse wxLogin(String code) {
        // 调用微信 code2session 接口
        Map<String, Object> wxResp = callCode2Session(code);
        String openid = (String) wxResp.get("openid");
        if (openid == null) {
            log.error("微信登录失败, resp: {}", wxResp);
            throw new BizException(ResultCode.WX_LOGIN_FAIL);
        }
        String unionid = (String) wxResp.get("unionid");

        // 查找或创建用户
        User existUser = userService.findByOpenid(openid);
        boolean isNew = existUser == null;
        User user = userService.findOrCreate(openid, unionid);

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getUserId());
        return new LoginResponse(token, user.getUserId(), isNew);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callCode2Session(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code);
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BizException(ResultCode.WX_LOGIN_FAIL);
        }
    }
}
