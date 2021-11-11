package com.arui.mall.core.cart.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.common.util.AuthContextHolder;
import com.arui.mall.core.cart.service.CartInfoService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@RestController
@RequestMapping("/cart")
public class CartInfoController {

    @Resource
    private CartInfoService cartInfoService;

    /**
     * 保存购物车信息，如果skuId和termId都有，优先保存skuId
     * @param skuId
     * @param skuNum
     * @param request
     * @return
     */
    @GetMapping("addCart/{skuId}/{skuNum}")
    public R addCart(@PathVariable Long skuId, @PathVariable Integer skuNum, HttpServletRequest request){
        // 临时id（点击添加购车的时候前端页面给的）
        String userTempId = AuthContextHolder.getUserTempId(request);
        //已经登录的userID,登录的也有可能有临时id
        String userId = AuthContextHolder.getUserId(request);

        // 最终userId, 如果userId 没有，那么就userTempId
        String finalUserId = StringUtils.isEmpty(userId)? userTempId : userId;

        // 保存finalId购物车信息
        cartInfoService.addTempIdCart(finalUserId, skuId, skuNum);
        return R.ok();
    }
}

