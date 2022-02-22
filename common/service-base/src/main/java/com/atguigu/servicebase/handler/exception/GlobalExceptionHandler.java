package com.atguigu.servicebase.handler.exception;

import com.atguigu.commonutils.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
        e.printStackTrace();
        return R.error();
    }


    @ExceptionHandler(GuiGuException.class)
    @ResponseBody
    public R error(GuiGuException e){
        log.error("业务出现异常:",e);
        return R.error().message(e.getMsg()).code(e.getCode());
    }
}
