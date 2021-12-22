package com.atguigu.yygh.msm.service.impl;

import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.ConstantPropertiesUtils;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sound.midi.Track;
import java.util.HashMap;
import java.util.Set;

@Service
public class MsmServiceImpl implements MsmService {

    //发送手机验证码
    @Override
    public Boolean send(String phone, String code) {
        //判断手机号空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合短信
        //设置相关参数
        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = ConstantPropertiesUtils.ACCOUNTSID; // 需修改
        String accountToken = ConstantPropertiesUtils.AUTHTOKEN; // 需修改
        //请使用管理控制台中已创建应用的APPID
        String appId = ConstantPropertiesUtils.APP_ID;  // 需修改
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        // 用逗号隔开手机号，官方说最多写三个手机号，似乎能实现群发的效果，但是我没做测试
        //String to = "13957757545,17829397989";
        String templateId = "1"; // 测试模板写死是1
        String[] datas = {code, "3"}; // datas就是发过去短信中的模板，code就是服务端生成的验证码， 3表示几分钟内有效
        // 下面这些可选的都不要选，否则会出问题
        //String subAppend="1234";  //可选 扩展码，四位数字 0~9999
        //String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        // 这里的phone就是手机号码，我这是方法入参接收的，变量名为phone
        HashMap<String, Object> result = sdk.sendTemplateSMS(phone, templateId, datas/*,subAppend,reqId*/);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return true;
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            return false;
        }
    }
}
