package com.arui.mall.core.cart.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.common.util.AuthContextHolder;
import com.arui.mall.core.cart.service.CartInfoService;
import com.arui.mall.model.pojo.entity.CartInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.models.auth.In;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        // 获取userId
        String finalUserId = getFinalUserId(request);
        // 保存finalId购物车信息
        cartInfoService.addTempIdCart(finalUserId, skuId, skuNum);
        return R.ok();
    }

    /**
     * 获取最终的userId, userId 比userTempId 优先
     * @param request
     * @return
     */
    private String getFinalUserId(HttpServletRequest request) {
        // 临时id（点击添加购车的时候前端页面给的）
        String userTempId = AuthContextHolder.getUserTempId(request);
        //已经登录的userID,登录的也有可能有临时id
        String userId = AuthContextHolder.getUserId(request);

        // 最终userId, 如果userId 没有，那么就userTempId
        return StringUtils.isEmpty(userId)? userTempId : userId;
    }

    /**
     * 展示购物车列表, 未登录的，如果再购物车列表登录，需要合并，userId和tempId
     * @param request
     * @return
     */
    @GetMapping("getCartList")
    public R getCartList(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId, userTempId);
        return R.ok(cartInfoList);
    }

    /**
     * http://api.gmall.com/cart//checkCart/28/1
     * 选中商品，1勾选，0取消勾选
     * @param skuId
     * @param flag
     * @return
     */
    @GetMapping("checkCart/{skuId}/{flag}")
    public R checkCart(@PathVariable Long skuId, @PathVariable Integer flag){
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("sku_id", skuId);
        CartInfo cartInfo = cartInfoService.getOne(cartInfoQueryWrapper);
        cartInfo.setIsChecked(flag);
        cartInfoService.updateById(cartInfo);
        return R.ok();
    }

    /**
     * http://api.gmall.com/cart//deleteCart/24
     * 删除购物项
     * @param skuId
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public R deleteCart(@PathVariable Long skuId){
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("sku_id", skuId);
        cartInfoService.remove(cartInfoQueryWrapper);
        return R.ok();
    }
}

