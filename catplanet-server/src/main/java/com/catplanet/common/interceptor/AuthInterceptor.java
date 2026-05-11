package com.catplanet.common.interceptor;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.isValid(token)) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        Long userId = jwtUtil.parseUserId(token);
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
