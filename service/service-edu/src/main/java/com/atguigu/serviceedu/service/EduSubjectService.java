package com.atguigu.serviceedu.service;

import com.atguigu.serviceedu.entity.EduSubject;
import com.atguigu.serviceedu.vo.SubjectNestedVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-14
 */
public interface EduSubjectService extends IService<EduSubject> {

    void importSubjectData(MultipartFile file);

    List<SubjectNestedVo> nestedList();

}
