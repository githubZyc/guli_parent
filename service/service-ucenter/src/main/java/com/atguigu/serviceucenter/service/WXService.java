package com.atguigu.serviceucenter.service;

import javax.servlet.http.HttpSession;

public interface WXService {
    String getQRContent(HttpSession session);

    String getWXUserInfo(String code, String state);
}
