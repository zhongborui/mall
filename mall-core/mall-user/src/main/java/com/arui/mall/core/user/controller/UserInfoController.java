package com.arui.mall.core.user.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.common.util.IpUtil;
import com.arui.mall.core.user.service.UserInfoService;
import com.arui.mall.model.pojo.entity.UserInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import sun.net.util.IPAddressUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * http://api.gmall.com/user/login
     * 登录验证, 如果验证通过，将token存到redis，和cookie中
     * @param userInfo
     * @return
     */
    @PostMapping("/login")
    public R loginCheck(@RequestBody UserInfo userInfo, HttpServletRequest request){
        String ip = IpUtil.getIpAddress(request);
        Map<String, String> retMap = userInfoService.loginCheck(userInfo, ip);
        return R.ok(retMap);
    }
}

