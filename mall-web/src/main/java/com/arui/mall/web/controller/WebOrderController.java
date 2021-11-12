package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.OrderFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单确认页面
 * @author ...
 */
@Controller
public class WebOrderController {
    @Resource
    private OrderFeignClient orderFeignClient;

    @GetMapping("/confirm.html")
    public String confirm(Model model){
        R<Map> r = orderFeignClient.showConfirmPage();
        Map data = r.getData();
        model.addAllAttributes(data);
        return "order/confirm";
    }
}
