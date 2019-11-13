package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.serivice.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanglei
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;


    @RequestMapping("accredit")
    public Resp<Object> authentication(@RequestParam("username")String userName, @RequestParam("password")String password, HttpServletRequest request, HttpServletResponse response){
        String jwtToken = this.authService.authentication(userName, password);
        if(StringUtils.isEmpty(jwtToken)){
            return Resp.fail("xxxx");
        }

        //4.把生成的jwt放入cookie中
        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),jwtToken,this.jwtProperties.getExpire()*60);

        return Resp.ok(null);
    }

}
