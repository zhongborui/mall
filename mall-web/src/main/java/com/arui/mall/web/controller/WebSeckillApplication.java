package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.SecKillFeignClient;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @author ...
 */
@Controller
public class WebSeckillApplication {

    @Resource
    private SecKillFeignClient secKillFeignClient;

    /**
     * 展示秒杀商品列表
     *
     * @param model
     * @return
     */
    @GetMapping("seckill-index.html")
    public String seckillIndex(Model model) {
        R<List<SeckillProduct>> resData = secKillFeignClient.queryAllSecKillProduct();
        model.addAttribute("list", resData.getData());
        return "seckill/index";
    }


    /**
     * 展示秒杀商品详情页
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("seckill-detail/{skuId}.html")
    public String seckillDetail(@PathVariable Long skuId, Model model) {
        R<SeckillProduct> seckillProductR = secKillFeignClient.querySecKillProduct(skuId);
        model.addAttribute("item", seckillProductR.getData());
        return "seckill/detail";
    }

    /**
     * 点击抢购后，到排队页面或成功页面
     *
     * @param skuId
     * @param seckillCode
     * @param model
     * @return
     */
    @GetMapping("seckill-queue.html")
    public String seckillQueue(Long skuId, String seckillCode, Model model) {
        model.addAttribute("skuId", skuId);
        model.addAttribute("seckillCode", seckillCode);
        return "seckill/queue";
    }


    /**
     * 秒杀确认页面
     * @param skuId
     * @param seckillCode
     * @param model
     * @return
     */
    @GetMapping("seckill-confirm.html")
    public String seckillConfirm(Long skuId, String seckillCode, Model model) {
        R retVal = secKillFeignClient.seckillConfirm();
        Map<String, Object> retMap = (Map<String, Object>) retVal.getData();
        model.addAllAttributes(retMap);
        return "seckill/confirm";
    }
}
