package com.catplanet.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(0, "success"),
    UNAUTHORIZED(401, "未登录或 token 已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    FAMILY_NOT_FOUND(10001, "家庭不存在"),
    FAMILY_MEMBER_EXISTS(10002, "已是家庭成员"),
    INVITE_CODE_INVALID(10003, "邀请码无效"),
    CAT_NOT_FOUND(10004, "猫咪不存在"),
    WX_LOGIN_FAIL(10005, "微信登录失败"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;
}
