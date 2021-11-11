package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ...
 */

@Controller
public class CartWebController {

    @GetMapping("addCart.html")
    public String addCart(){
        return "cart/addCart";
    }
}
