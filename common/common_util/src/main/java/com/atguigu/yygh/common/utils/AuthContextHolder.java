package com.atguigu.yygh.common.utils;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

//获取当前用户信息工具类
public class AuthContextHolder {

    //获取当前用户id
    public static Long getUserId(HttpServletRequest request) {
        //从header里获取token
        String token = request.getHeader("token");
        //Jwt从token里获取userid
        return JwtHelper.getUserId(token);
    }

    //获取当前用户名称
    public static String getUserName(HttpServletRequest request) {
        //从header里获取token
        String token = request.getHeader("token");
        //Jwt从token里获取userid
        return JwtHelper.getUserName(token);
    }
}
