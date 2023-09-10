package com.yxq.utils.common;

import com.yxq.model.wemedia.pojos.WmUser;

/**
 * @Description:
 * @Author: yxq
 * @Date: 2023/9/10
 */
public class WmThreadLocalUtil {
    private final static ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    //新增
    public static void setUser(WmUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    //获取
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    //删除
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
