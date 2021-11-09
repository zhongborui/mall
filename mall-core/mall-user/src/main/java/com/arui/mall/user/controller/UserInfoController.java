package com.arui.mall.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.arui.mall.common.result.R;
import com.arui.mall.common.util.IpUtil;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.model.pojo.entity.UserInfo;
import com.arui.mall.user.service.UserInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-08
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 登录验证
     *
     * @param userInfo
     * @return
     */
    @PostMapping("login")
    public R login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        UserInfo userInfoFromDB = userInfoService.login(userInfo);
        if (userInfoFromDB != null) {
            Map<String, Object> retMap = new HashMap<>();
            String token = UUID.randomUUID().toString();
            // 将信息放到token中
            retMap.put("token", token);
            retMap.put("nickName", userInfoFromDB.getNickName());

            // 同时存到缓存中
            String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
            Map<String, Object> redisMap = new HashMap<>();
            String ip = IpUtil.getIpAddress(request);
            Long id = userInfoFromDB.getId();
            redisMap.put("ip", ip);
            redisMap.put("userId", id);
            String redisStr = JSONObject.toJSONString(redisMap);
            redisTemplate.opsForValue().set(userKey, redisStr, RedisConstant.USERKEY_TIMEOUT, TimeUnit.SECONDS);

            return R.ok(retMap);
        } else {
            return R.fail("登录失败!");
        }
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("logout")
    public R logout(HttpServletRequest request){
        String token = request.getHeader("token");
        String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
        redisTemplate.delete(userKey);
        return R.ok();
    }
}

