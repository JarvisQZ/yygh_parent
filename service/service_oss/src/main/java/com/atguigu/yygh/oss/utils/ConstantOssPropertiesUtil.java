package com.atguigu.yygh.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantOssPropertiesUtil implements InitializingBean {

    //阿里云OSS配置
    @Value("${aliyun.oss.endpoint}")
    private String endpoint = "oss-cn-beijing.aliyuncs.com";
    @Value("${aliyun.oss.accessKeyId}")
    private  String accessKeyId = "LTAI5tBfP9BkvfDLZpPnmULH";
    @Value("${aliyun.oss.secret}")
    private  String secret = "y7c6EWj1zhRWYivTruv4umIb7p27M5";
    @Value("${aliyun.oss.bucket}")
    private  String bucket = "qz-yygh";

    public static String ENDPOINT;
    public static String ACCESS_KEY_ID;
    public static String SECRET;
    public static String BUCKET;


    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = endpoint;
        ACCESS_KEY_ID = accessKeyId;
        SECRET = secret;
        BUCKET = bucket;
    }
}
