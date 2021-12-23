package com.atguigu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    //容联云配置信息
    private final String appId = "8aaf07089d0003f";
    private final String accountSId = "8a216da87deae1ef50014";
    private final String authToken = "5a695d8003663b108";
    private final String appToken = "000d2fd683157131";

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
