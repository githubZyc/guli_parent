package com.atguigu.serviceucenter.service.impl;

import com.alibaba.nacos.common.util.Md5Utils;
import com.atguigu.commonutils.tool.JWTUtils;
import com.atguigu.commonutils.vo.R;
import com.atguigu.servicebase.handler.exception.GuiGuException;
import com.atguigu.serviceucenter.entity.UcenterMember;
import com.atguigu.serviceucenter.mapper.UcenterMemberMapper;
import com.atguigu.serviceucenter.service.UcenterMemberService;
import com.atguigu.serviceucenter.vo.LoginInfoVo;
import com.atguigu.serviceucenter.vo.LoginVo;
import com.atguigu.serviceucenter.vo.RegisterVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-18
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 功能描述: 登陆->成功返回当前用户token
     * @Author ZYC
     * @Date 2022/2/21 13:57
     * @Param [loginVo]
     * @Return java.lang.String
     * @Version 1.0
     **/
    @Override
    public String login(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //校验参数
        if(StringUtils.isEmpty(mobile) ||StringUtils.isEmpty(password)) {
            throw new GuiGuException(20001,"必要参数未传递");
        }
        //获取会员
        UcenterMember member = baseMapper.selectOne(new QueryWrapper<UcenterMember>().eq("mobile", mobile));
        if(null == member) {
            throw new GuiGuException(20001,"用户或密码不存在");
        }
        //校验密码
        if(!Md5Utils.getMD5(password.getBytes()).equals(member.getPassword())) {
            throw new GuiGuException(20001,"密码错误");
        }
        //校验是否被禁用
        if(member.getIsDisabled()) {
            throw new GuiGuException(20001,"用户已禁用");
        }
        //使用JWT生成token字符串
        String token = JWTUtils.getJwtToken(member.getId(), member.getNickname());
        return token;
    }

    @Override
    public void register(RegisterVo registerVo) {
        //获取注册信息，进行校验
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        //校验参数
        if(StringUtils.isEmpty(nickname)||StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password) ||StringUtils.isEmpty(code)) {
            throw new GuiGuException(20001,"必要参数未传递");
        }

        //校验校验验证码
        //从redis获取发送的验证码
        String mobileCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(mobileCode)) {
            throw new GuiGuException(20001,"验证码有误");
        }
        //查询数据库中是否存在相同的手机号码
        Integer count = baseMapper.selectCount(new QueryWrapper<UcenterMember>().eq("mobile", mobile));
        if(count> 0) {
            throw new GuiGuException(20001,"用户已存在");
        }

        //添加注册信息到数据库
        UcenterMember member = new UcenterMember();
        member.setNickname(nickname);
        member.setMobile(registerVo.getMobile());
        member.setPassword(Md5Utils.getMD5(password.getBytes()));
        member.setIsDisabled(false);
        member.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        this.save(member);
    }

    /**
     * 功能描述: 获取用户信息
     * @Author ZYC
     * @Date 2022/2/21 14:14
     * @Param [request]
     * @Return com.atguigu.serviceucenter.vo.LoginInfoVo
     * @Version 1.0
     **/
    @Override
    public R getLoginInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        String memberId = JWTUtils.getMemberIdByJwtToken(token);
        if(StringUtils.isEmpty(memberId)){
            throw new GuiGuException(20001,"token错误");
        }
        UcenterMember byId = this.getById(memberId);
        return R.ok().data("item",LoginInfoVo.builder().id(byId.getId()).nickName(byId.getNickname()).build());
    }

    /**
     * 功能描述: 根据appId获取用户信息
     * @Author ZYC
     * @Date 2022/2/21 17:35
     * @Param [openid]
     * @Return com.atguigu.serviceucenter.entity.UcenterMember
     * @Version 1.0
     **/
    @Override
    public UcenterMember getByOpenid(String openid) {
        QueryWrapper<UcenterMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        UcenterMember member = baseMapper.selectOne(queryWrapper);
        return member;
    }
}
