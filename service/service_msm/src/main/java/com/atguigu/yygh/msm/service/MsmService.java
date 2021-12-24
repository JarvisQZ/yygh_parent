package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface MsmService {
    //发送手机验证码
    Boolean send(String phone, String code);

    //mq使用的发送短信的接口
    boolean send(MsmVo msmVo);
}
