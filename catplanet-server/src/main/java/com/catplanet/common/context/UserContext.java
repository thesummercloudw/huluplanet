package com.catplanet.common.context;

/**
 * 线程级上下文，保存当前请求用户ID和家庭ID
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> FAMILY_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setFamilyId(Long familyId) {
        FAMILY_ID.set(familyId);
    }

    public static Long getFamilyId() {
        return FAMILY_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
        FAMILY_ID.remove();
    }
}
