package com.arui.mall.core.user.service.impl;

import com.arui.mall.core.user.mapper.UserAddressMapper;
import com.arui.mall.core.user.service.UserAddressService;
import com.arui.mall.model.pojo.entity.UserAddress;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

}
