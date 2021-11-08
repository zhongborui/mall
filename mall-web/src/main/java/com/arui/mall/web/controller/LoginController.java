package com.arui.mall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ...
 */
@Controller
public class LoginController {

    /**
     * 登录页面
     * @return
     */
    @GetMapping("/login.html")
    public String login(HttpServletRequest request){
        String originalUrl = request.getParameter("originalUrl");
        request.setAttribute("originalUrl", originalUrl);
        return "login";
    }

}
