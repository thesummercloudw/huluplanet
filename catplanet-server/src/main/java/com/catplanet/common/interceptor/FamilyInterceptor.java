package com.catplanet.common.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.family.entity.FamilyMember;
import com.catplanet.module.family.mapper.FamilyMemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 家庭隔离拦截器：优先从 X-Family-Id Header 取，缺失时自动取用户的第一个家庭
 */
@Component
@RequiredArgsConstructor
public class FamilyInterceptor implements HandlerInterceptor {

    private final FamilyMemberMapper familyMemberMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String familyIdStr = request.getHeader("X-Family-Id");

        if (StringUtils.hasText(familyIdStr)) {
            try {
                UserContext.setFamilyId(Long.parseLong(familyIdStr));
            } catch (NumberFormatException e) {
                throw new BizException(ResultCode.BAD_REQUEST.getCode(), "X-Family-Id 格式错误");
            }
        } else {
            // 未传 Header 时，自动取当前用户的第一个家庭
            Long userId = UserContext.getUserId();
            if (userId != null) {
                List<FamilyMember> members = familyMemberMapper.selectList(
                        new LambdaQueryWrapper<FamilyMember>()
                                .eq(FamilyMember::getUserId, userId)
                                .orderByAsc(FamilyMember::getJoinedAt)
                                .last("LIMIT 1"));
                if (!members.isEmpty()) {
                    UserContext.setFamilyId(members.get(0).getFamilyId());
                }
            }
        }
        return true;
    }
}
