package com.arui.mall.user.service;

import com.arui.mall.model.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-08
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 验证登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);
}
