package com.atguigu.serviceucenter.controller.wx;

import com.atguigu.serviceucenter.service.WXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin
@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/ucenter/wx")
public class WxApiController {
    @Autowired
    private WXService wxService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session) {
        return wxService.getQRContent(session);
    }

    @ResponseBody
    @GetMapping(value = "callback",produces = "application/json")
    public String genQrConnect(String code, String state) {
        return wxService.getWXUserInfo(code,state);
    }
}
