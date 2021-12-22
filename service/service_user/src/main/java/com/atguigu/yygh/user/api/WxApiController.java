package com.atguigu.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtils;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");//System.currentTimeMillis()+""
        return Result.ok(map);
    }

    //微信扫码后进行回调的方法
    @GetMapping("callback")
    public String callback(String code, String state) {
        //第一步，获取临时票据 code
        System.out.println("code: " + code);
        //第二步，拿着code和微信id和密钥，请求微信固定地址，得到两个值
        //使用code和 appid 以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        //使用httpclient
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo: " + accesstokenInfo);
            //从返回的字符串中获取两个值 openid 和 access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String accessToken = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");

            //判断数据库中是否存在微信的扫码人信息
            //根据openid判断
            UserInfo userInfo = userInfoService.getWxInfoOpenId(openId);
            if (userInfo == null) {//数据库不存在
                //第三步，使用openid和accessToken 请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);

                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo " + resultInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //获取扫码人信息，添加数据库
                userInfo = new UserInfo();
                userInfo.setOpenid(openId);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }


            //返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);

            //判断userInfo是否有手机号吗如果手机号为空，返回openid 进行绑定
            //如果手机号不为空，返回openid值是空字符串
            if (StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }

            //使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            //跳转到前端页面
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL
                    + "/weixin/callback?token=" + map.get("token")
                    + "&openid=" + map.get("openid") + "&name="
                    + URLEncoder.encode((String) map.get("name"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
