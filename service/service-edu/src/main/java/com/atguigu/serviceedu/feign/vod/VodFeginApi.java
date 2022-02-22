package com.atguigu.serviceedu.feign.vod;

import com.atguigu.commonutils.vo.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-vod",fallback = VodFeginApiHystrix.class)
public interface VodFeginApi {
    String SERVER_NAME = "/vod";

    @GetMapping(SERVER_NAME + "/video/videoInfo")
    R videoInfo(@ApiParam(name = "videoId", value = "当前页码", required = true)
                       @RequestParam(defaultValue = "1") String videoId);
}
