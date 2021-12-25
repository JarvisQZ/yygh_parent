package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeixinService;
import com.atguigu.yygh.order.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.order.utils.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RedisTemplate redisTemplate;

    //生成微信支付二维码
    @Override
    public Map<String, Object> createNative(Long orderId) {

        try {
            //从redis里获取数据
            Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if (null != payMap) {
                return payMap;
            }

            //1. 根据orderId获取订单信息
            OrderInfo order = orderService.getById(orderId);

            //2. 向支付记录表添加信息
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
            //3. 设置参数,调用微信生成二维码的接口
            // 把参数转化成xml格式,使用商户key进行加密
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = order.getReserveDate() + "就诊" + order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//为了测试,统一写成这个值
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");//扫码支付

            //4. 调用微信生成二维码的接口, httpclient进行调用
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");//传入微信支付地址
            // 设置map里的参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();//完成发送请求

            //5. 微信返回相关数据, 也是xml文件
            String xml = client.getContent();
            //转换xml => map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("resultMap: " + resultMap);

            //6. 封装返回的结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));//二维码的地址

            if (null != resultMap.get("result_code")) {
                redisTemplate.opsForValue().set(orderId.toString(), map, 120, TimeUnit.MINUTES);
            }

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //调用微信接口实现支付状态的查询
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            //1 根据order获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);

            //2 封装提交参数
            HashMap paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            //3 设置请求内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //4 得到微信接口返回数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            //5 把接口数据返回
            System.out.println("支付状态resultMap: " + resultMap);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
