package com.atguigu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    //容联云配置信息
    private final String appId = "8aaf03f";
    private final String accountSId = "8a2114";
    private final String authToken = "5a68";
    private final String appToken = "001";

    public static String APP_ID;
    public static String APP_TOKEN;
    public static String ACCOUNTSID;
    public static String AUTHTOKEN;


    @Override
    public void afterPropertiesSet() throws Exception {
        APP_ID = appId;
        APP_TOKEN = appToken;
        ACCOUNTSID = accountSId;
        AUTHTOKEN = authToken;
    }
}
