package com.dx1ngy.trading.api.utils;

import cn.dev33.satoken.stp.StpLogic;

public class StpUtil {

    public static final String USER_TYPE = "user";

    /**
     * User 会话对象，管理 User 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogic(USER_TYPE);

}
