package com.arui.mall.core.user.service;

import com.arui.mall.model.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 登录验证
     * @param userInfo
     * @param ip
     * @return
     */
    Map<String, String> loginCheck(UserInfo userInfo, String ip);
}
