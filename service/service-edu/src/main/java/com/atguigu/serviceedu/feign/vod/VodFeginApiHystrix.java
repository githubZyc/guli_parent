package com.atguigu.serviceedu.feign.vod;

import com.atguigu.commonutils.vo.R;
import org.springframework.stereotype.Component;

@Component
public class VodFeginApiHystrix implements VodFeginApi {
    @Override
    public R videoInfo(String videoId) {
        return R.error().message("服务暂时不可用，稍后重试");
    }
}
