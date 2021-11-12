package com.arui.mall.feign.client;

import com.arui.mall.model.pojo.entity.UserAddress;
import com.arui.mall.model.pojo.entity.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ...
 */
@FeignClient(value = "mall-user")
public interface UserFeignClient {

    /**
     * 返回用户地址信息
     * @param userId
     * @return
     */
    @GetMapping("/user/getUserAddressList/{userId}")
    public List<UserAddress> getUserAddressList(@PathVariable Long userId);

    /**
     * 返回用户基本信息
     * @param userId
     * @return
     */
    @GetMapping("/user/getUserInfo/{userId}")
    public UserInfo getUserInfo(@PathVariable Long userId);
}
