package com.atguigu.servicebase.constant;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    NO_DATA(10001,"无数据"),
    FILE_UPLOAD_ERROR(20001,"上传失败"),
    FILE_READ_ERROR(20002,"添加课程分类失败")

            ;
    public Integer code;
    public String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
