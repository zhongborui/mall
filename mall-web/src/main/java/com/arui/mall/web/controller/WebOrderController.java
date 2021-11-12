package com.arui.mall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 订单确认页面
 * @author ...
 */
@Controller
public class WebOrderController {



    @GetMapping("/confirm.html")
    public String confirm(){
        return "order/confirm";
    }
}
