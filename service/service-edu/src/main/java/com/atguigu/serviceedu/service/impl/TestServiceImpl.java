package com.atguigu.serviceedu.service.impl;

import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceedu.feign.vod.VodFeginApi;
import com.atguigu.serviceedu.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    VodFeginApi vodFeginApi;

    /**
     * 功能描述: 获取Video Info
     * @Author ZYC
     * @Date 2022/2/16 16:32
     * @Param [vodId]
     * @Return com.atguigu.commonutils.vo.R
     * @Version 1.0
     **/
    @Override
    public R testFeign(String vodId) {
        return vodFeginApi.videoInfo(vodId);
    }
}
