package com.arui.mall.cart.controller;


import com.arui.mall.cart.service.CartInfoService;
import com.arui.mall.common.result.R;
import com.arui.mall.common.util.AuthContextHolder;
import io.swagger.models.auth.In;
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
 * @since 2021-11-09
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
        String userTempId = request.getHeader("userTempId");
        //已经登录的userID,登录的也有临时id
        String userId = request.getHeader("userId");
        String userId1 = AuthContextHolder.getUserId(request);
        String userTempId1 = AuthContextHolder.getUserTempId(request);
        // 最终userId
        String finalUserId = null;

        if (!StringUtils.isEmpty(userTempId)) {
            //这是添加购物车前已经登录了，就没有tempId
            finalUserId = userId;
        }
        // 保存临时id购物车信息
        cartInfoService.addTempIdCart(userTempId, skuId, skuNum);
        return null;
    }

}

