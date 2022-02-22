package com.atguigu.serviceedu.controller;

import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceedu.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 测试使用控制器
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-14
 */
@Api("测试使用控制器")
@RestController
@RequestMapping("/test")
public class Test {
    @Autowired
    TestService testService;

    @GetMapping
    public R testFeign(@ApiParam(name = "vodId", value = "视频id", required = true)@RequestParam String vodId){
        return testService.testFeign(vodId);
    }
}
