package com.atguigu.serviceucenter.service.impl;

import com.atguigu.commonutils.tool.HttpUtils;
import com.atguigu.commonutils.tool.JWTUtils;
import com.atguigu.servicebase.handler.exception.GuiGuException;
import com.atguigu.serviceucenter.config.ConstantProperties;
import com.atguigu.serviceucenter.entity.UcenterMember;
import com.atguigu.serviceucenter.service.UcenterMemberService;
import com.atguigu.serviceucenter.service.WXService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@Slf4j
@Service
public class WXServiceImpl implements WXService {
    @Autowired
    private UcenterMemberService ucenterMemberService;

    @Autowired
    private ConstantProperties constantProperties;
    @Override
    public String getQRContent(HttpSession session) {
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        // 回调地址
        String redirectUrl = constantProperties.getRedirectUrl(); //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new GuiGuException(20001, e.getMessage());
        }
        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟
        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                constantProperties.getAppId(),
                redirectUrl,
                state);
        log.debug("qrcodeUrl ----{}---- ",qrcodeUrl);
        return "redirect:" + qrcodeUrl;
    }

    /**
     * 功能描述: 通过access_token 获取微信用户基本信息
     * @Author ZYC
     * @Date 2022/2/21 16:20
     * @Param [code, state]
     * @Return java.lang.String
     * @Version 1.0
     **/
    @Override
    public String getWXUserInfo(String code, String state) {
        System.out.println(code);
        System.out.println(state);

        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
        "?appid=%s" +
        "&secret=%s" +
        "&code=%s" +
        "&grant_type=authorization_code";
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                constantProperties.getAppId(),
                constantProperties.getAppSecret(),
                code);

        String result = null;
        try {
            /**
             * 第二步：通过code获取access_token
             * 正确返回结果:
             * {
             * "access_token":"ACCESS_TOKEN",
             * "expires_in":7200,
             * "refresh_token":"REFRESH_TOKEN",
             * "openid":"OPENID",
             * "scope":"SCOPE",
             * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
             * }
             */
            result = HttpUtils.get(accessTokenUrl);
            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
            throw new GuiGuException(20001, "获取access_token失败");
        }

        //解析json字符串
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String)map.get("access_token");
        String openid = (String)map.get("openid");

        //查询数据库当前用用户是否曾经使用过微信登录
        UcenterMember member = ucenterMemberService.getByOpenid(openid);
        if(member == null){
            System.out.println("新用户注册");
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
            "?access_token=%s" +
            "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                throw new GuiGuException(20001, "获取用户信息失败");
            }
            //解析json
            HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
            String nickname = (String)mapUserInfo.get("nickname");
            String headimgurl = (String)mapUserInfo.get("headimgurl");
            //向数据库中插入一条记录
            UcenterMember ucenterMember = new UcenterMember();
            ucenterMember.setNickname(nickname);
            ucenterMember.setOpenid(openid);
            ucenterMember.setAvatar(headimgurl);
            ucenterMemberService.save(ucenterMember);
        }
        // 生成jwt  可能会印发空指针  member 需要被重新赋值
        String token = JWTUtils.getJwtToken(member.getId(),member.getNickname());
        //存入cookie
        //CookieUtils.setCookie(request, response, "guli_jwt_token", token);
        //因为端口号不同存在蛞蝓问题，cookie不能跨域，所以这里使用url重写
        return "redirect:http://localhost:3000?token=" + token;
    }
}
