package com.atguigu.serviceucenter.service;

import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceucenter.entity.UcenterMember;
import com.atguigu.serviceucenter.vo.LoginInfoVo;
import com.atguigu.serviceucenter.vo.LoginVo;
import com.atguigu.serviceucenter.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-18
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    String login(LoginVo loginVo);

    void register(RegisterVo registerVo);

    R getLoginInfo(HttpServletRequest request);

    UcenterMember getByOpenid(String openid);
}
