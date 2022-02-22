package com.atguigu.serviceedu.service;

import com.atguigu.commonutils.vo.R;
import com.atguigu.serviceedu.dto.query.EduTeacherQuery;
import com.atguigu.serviceedu.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-10
 */
public interface EduTeacherService extends IService<EduTeacher> {

    R pageList(EduTeacherQuery teacherQuery);

}
