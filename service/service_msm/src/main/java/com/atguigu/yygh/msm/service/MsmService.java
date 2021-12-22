package com.atguigu.yygh.msm.service;

public interface MsmService {
    //发送手机验证码
    Boolean send(String phone, String code);
}
