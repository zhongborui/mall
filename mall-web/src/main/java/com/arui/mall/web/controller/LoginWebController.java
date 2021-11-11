package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ...
 */
@Controller
public class LoginWebController {

    @GetMapping("/login.html")
    public String loginWeb(HttpServletRequest request){
        String originalUrl = request.getParameter("originalUrl");
        request.setAttribute("originalUrl", originalUrl);
        return "login";
    }
}
