package com.arui.mall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ...
 */
@Controller
public class WebSeckillApplication {

    @GetMapping("seckill-index.html")
    public String seckillIndex(){
        return "seckill/index";
    }
}
