package com.atguigu.servicesms.service;

import java.util.Map;

public interface MsmService {
    boolean send(String phone, String sms, Map<String, Object> param);
}
