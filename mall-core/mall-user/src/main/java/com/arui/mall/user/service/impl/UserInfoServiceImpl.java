package com.arui.mall.user.service.impl;


import com.arui.mall.model.pojo.entity.UserInfo;
import com.arui.mall.user.mapper.UserInfoMapper;
import com.arui.mall.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo login(UserInfo userInfo) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        String encodePasswd = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfoQueryWrapper.eq("login_name", userInfo.getLoginName())
                .eq("passwd", encodePasswd);
        UserInfo userInfoFromDB = baseMapper.selectOne(userInfoQueryWrapper);

        return userInfoFromDB;
    }
}
