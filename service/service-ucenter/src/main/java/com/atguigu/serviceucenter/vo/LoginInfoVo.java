package com.atguigu.serviceucenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginInfoVo {
    @ApiModelProperty(value = "id")
    String id;
    @ApiModelProperty(value = "用户昵称")
    String nickName;
}
