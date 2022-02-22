package com.atguigu.serviceucenter.controller;


import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceucenter.service.UcenterMemberService;
import com.atguigu.serviceucenter.vo.LoginInfoVo;
import com.atguigu.serviceucenter.vo.LoginVo;
import com.atguigu.serviceucenter.vo.RegisterVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-18
 */
@RestController
@RequestMapping("/member")
public class MemberApiController {
    @Autowired
    private UcenterMemberService ucenterMemberService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        String token = ucenterMemberService.login(loginVo);
        return R.ok().data("token", token);
    }

    @ApiOperation(value = "会员注册")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        ucenterMemberService.register(registerVo);
        return R.ok();
    }

    @ApiOperation(value = "根据token获取登录信息")
    @GetMapping("auth/getLoginInfo")
    public R getLoginInfo(HttpServletRequest request){
        return ucenterMemberService.getLoginInfo(request);
    }
}

