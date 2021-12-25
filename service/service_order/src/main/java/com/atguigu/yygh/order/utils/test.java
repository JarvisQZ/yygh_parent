package com.atguigu.yygh.order.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) {
        System.out.println(ConstantPropertiesUtils.CERT);
        ClassPathResource classPathResource = new ClassPathResource("cert/apiclient_cert.p12");
        try {
            File file = classPathResource.getFile();
            System.out.println(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
