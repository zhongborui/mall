package com.arui.mall.core.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.user.mapper.UserInfoMapper;
import com.arui.mall.core.user.service.UserInfoService;
import com.arui.mall.model.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录验证
     * @param userInfo
     * @param ip
     * @return
     */
    @Override
    public Map<String, String> loginCheck(UserInfo userInfo, String ip) {
        // 加密密码
        String encodePasswd = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("login_name", userInfo.getLoginName() ).eq("passwd", encodePasswd);
        UserInfo userInfoFromDb = baseMapper.selectOne(userInfoQueryWrapper);

        // 验证失败，直接返回
        if (userInfoFromDb == null){
            return null;
        }

        // uuid作为token标识
        String token = UUID.randomUUID().toString();
        // 验证通过，将用户信息存到token和redis(ip+userId)
        saveLoginTokenToRedis(ip, userInfoFromDb, token);
        Map<String, String> retMap = new HashMap<>();
        retMap.put("nickName", userInfoFromDb.getNickName());
        retMap.put("token", token);
        return retMap;
    }

    /**
     * 退出
     * @param token
     */
    @Override
    public void logout(String token) {
        String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
        redisTemplate.delete(userKey);
    }

    /**
     * // 验证通过，将用户信息存到token和redis(ip+userId)
     * @param ip
     * @param userInfoFromDb
     */
    private void saveLoginTokenToRedis(String ip, UserInfo userInfoFromDb, String token) {
        Map<String, Object> redisMap = new HashMap<>();
        redisMap.put("ip", ip);
        redisMap.put("userId", userInfoFromDb.getId());
        String jsonString = JSONObject.toJSONString(redisMap);
        String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(userKey, jsonString);
    }
}
