package com.atguigu.serviceedu.controller;


import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceedu.dto.query.EduTeacherQuery;
import com.atguigu.serviceedu.entity.EduTeacher;
import com.atguigu.serviceedu.service.EduTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-10
 */
@Api("edu-teacher控制接口")
@RestController
@RequestMapping("/teacher")
public class EduTeacherController {
    @Autowired
    private EduTeacherService eduTeacherService;

    @ApiOperation(value = "分页讲师列表")
    @PostMapping("/pageList")
    public R pageList(
            @Validated @RequestBody EduTeacherQuery teacherQuery) {
        return eduTeacherService.pageList(teacherQuery);
    }

    /**
     * 功能描述: 新增讲师
     *
     * @Author ZYC
     * @Date 2022/2/10 13:56
     * @Param []
     * @Return java.util.List<com.atguigu.serviceedu.entity.EduTeacher>
     * @Version 1.0
     **/
    @ApiOperation(value = "新增讲师")
    @PostMapping
    public R add(@ApiParam(name = "eduTeacher", value = "讲师", required = true) @RequestBody EduTeacher eduTeacher) {
        eduTeacherService.save(eduTeacher);
        return R.ok();
    }


    /**
     * 功能描述: 获取讲师列表
     *
     * @Author ZYC
     * @Date 2022/2/10 13:56
     * @Param []
     * @Return java.util.List<com.atguigu.serviceedu.entity.EduTeacher>
     * @Version 1.0
     **/
    @ApiOperation(value = "所有讲师列表")
    @GetMapping
    public R list() {
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items", list);
    }

    /**
     * 功能描述: 删除讲师
     *
     * @Author ZYC
     * @Date 2022/2/10 13:58
     * @Param [id]
     * @Return boolean
     * @Version 1.0
     **/
    @ApiOperation(value = "根据ID删除讲师")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable String id) {
        eduTeacherService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping("{id}")
    public R getById(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable String id) {
        System.out.println("根据ID查询讲师：{}"+id);
        EduTeacher teacher = eduTeacherService.getById(id);
        return R.ok().data("item", teacher);
    }

    @ApiOperation(value = "根据ID修改讲师")
    @PutMapping("{id}")
    public R updateById(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable String id,
            @ApiParam(name = "teacher", value = "讲师对象", required = true)
            @RequestBody EduTeacher teacher) {
        teacher.setId(id);
        eduTeacherService.updateById(teacher);
        return R.ok();
    }
}

