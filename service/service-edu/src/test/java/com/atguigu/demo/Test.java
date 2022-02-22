package com.atguigu.demo;

import com.atguigu.commonutils.tool.JWTUtils;

public class Test {
    @org.junit.Test
    public void main() {
        String jwtToken = JWTUtils.getJwtToken("001", "冥王");
        System.out.println("获取生成的token:"+jwtToken);

        boolean checkToken = JWTUtils.checkToken(jwtToken);

        if(checkToken){
            String memberIdByJwtToken = JWTUtils.getMemberIdByJwtToken(jwtToken);
            System.out.println(memberIdByJwtToken);
        }

    }
}
