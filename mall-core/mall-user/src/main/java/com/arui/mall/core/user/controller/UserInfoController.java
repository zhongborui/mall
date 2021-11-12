package com.arui.mall.core.user.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.common.util.IpUtil;
import com.arui.mall.core.user.service.UserAddressService;
import com.arui.mall.core.user.service.UserInfoService;
import com.arui.mall.model.pojo.entity.UserAddress;
import com.arui.mall.model.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;

import sun.net.util.IPAddressUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    @Resource
    private UserAddressService userAddressService;

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

    /**
     * http://api.gmall.com/user/logout
     * 退出
     * @param request
     * @return
     */
    @GetMapping("logout")
    public R logout(HttpServletRequest request){
        String token = request.getHeader("token");
        // redis中删除token
        userInfoService.logout(token);
        return R.ok();
    }

    /**
     * 返回用户地址信息
     * @param userId
     * @return
     */
    @GetMapping("getUserAddressList/{userId}")
    public List<UserAddress> getUserAddressList(@PathVariable Long userId){
        QueryWrapper<UserAddress> userAddressQueryWrapper = new QueryWrapper<>();
        userAddressQueryWrapper.eq("user_id", userId);
        List<UserAddress> userAddressList = userAddressService.list(userAddressQueryWrapper);
        return userAddressList;
    }

    /**
     * 返回用户基本信息
     * @param userId
     * @return
     */
    @GetMapping("getUserInfo/{userId}")
    public UserInfo getUserInfo(@PathVariable Long userId){
        UserInfo userInfo = userInfoService.getById(userId);
        return userInfo;
    }
}

