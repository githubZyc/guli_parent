package com.atguigu.serviceedu.vo;

import lombok.Data;

import java.util.List;

@Data
public class SubjectNestedVo {
    private String id;
    private String title;
    private List<SubjectVo> children;
}
