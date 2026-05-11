package com.catplanet.common.interceptor;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 家庭隔离拦截器：从 X-Family-Id Header 中提取家庭ID并校验
 */
@Component
public class FamilyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String familyIdStr = request.getHeader("X-Family-Id");
        if (!StringUtils.hasText(familyIdStr)) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "缺少 X-Family-Id 请求头");
        }
        try {
            Long familyId = Long.parseLong(familyIdStr);
            UserContext.setFamilyId(familyId);
        } catch (NumberFormatException e) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "X-Family-Id 格式错误");
        }
        return true;
    }
}
