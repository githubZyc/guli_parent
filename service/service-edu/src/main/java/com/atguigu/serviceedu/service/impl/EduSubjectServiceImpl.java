package com.atguigu.serviceedu.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.servicebase.constant.ResultCodeEnum;
import com.atguigu.servicebase.handler.exception.GuiGuException;
import com.atguigu.serviceedu.dto.excel.ExcelSubjectData;
import com.atguigu.serviceedu.entity.EduSubject;
import com.atguigu.serviceedu.listener.SubjectExcelListener;
import com.atguigu.serviceedu.mapper.EduSubjectMapper;
import com.atguigu.serviceedu.service.EduSubjectService;
import com.atguigu.serviceedu.vo.SubjectNestedVo;
import com.atguigu.serviceedu.vo.SubjectVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2022-02-14
 */
@Slf4j
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void importSubjectData(MultipartFile file) {
        try {
            //1 获取文件输入流
            InputStream inputStream = file.getInputStream();
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
            SubjectExcelListener subjectExcelListener = new SubjectExcelListener();
            EasyExcel.read(inputStream, ExcelSubjectData.class, subjectExcelListener).sheet().doRead();
            List<ExcelSubjectData> excelData = subjectExcelListener.getExcelData();
            for (ExcelSubjectData e:excelData) {
                String oneSubjectName = e.getOneSubjectName();
                EduSubject existOneSubject = this.existOneSubject(oneSubjectName);
                if(existOneSubject == null){
                    existOneSubject = new EduSubject();
                    existOneSubject.setTitle(e.getOneSubjectName());
                    existOneSubject.setParentId("0");
                    this.save(existOneSubject);
                }
                //获取一级分类id值
                String pid = existOneSubject.getId();

                //添加二级分类
                EduSubject existTwoSubject = this.existTwoSubject(e.getTwoSubjectName(), pid);
                if(existTwoSubject == null) {
                    existTwoSubject = new EduSubject();
                    existTwoSubject.setTitle(e.getTwoSubjectName());
                    existTwoSubject.setParentId(pid);
                    this.save(existTwoSubject);
                }
            }
        }catch(Exception e) {
            log.error("导入学科数据失败",e);
            throw new GuiGuException(ResultCodeEnum.FILE_READ_ERROR);
        }
    }

    /**
     * 按照树形结构展示学科分类
     * @return
     */
    @Override
    public List<SubjectNestedVo> nestedList() {
        //最终要的到的数据列表
        ArrayList<SubjectNestedVo> subjectNestedVoArrayList = new ArrayList<>();
        //获取一级分类数据记录
        QueryWrapper<EduSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        queryWrapper.orderByAsc("sort", "id");
        List<EduSubject> subjects = baseMapper.selectList(queryWrapper);
        //获取二级分类数据记录
        QueryWrapper<EduSubject> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.ne("parent_id", 0);
        queryWrapper2.orderByAsc("sort", "id");
        List<EduSubject> subSubjects = baseMapper.selectList(queryWrapper2);
        //填充一级分类vo数据
        int count = subjects.size();
        for (int i = 0; i < count; i++) {
            EduSubject subject = subjects.get(i);
            //创建一级类别vo对象
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
            BeanUtils.copyProperties(subject, subjectNestedVo);
            subjectNestedVoArrayList.add(subjectNestedVo);
            //填充二级分类vo数据
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            int count2 = subSubjects.size();
            for (int j = 0; j < count2; j++) {
                EduSubject subSubject = subSubjects.get(j);
                if(subject.getId().equals(subSubject.getParentId())){
                    //创建二级类别vo对象
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subSubject, subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }
            subjectNestedVo.setChildren(subjectVoArrayList);
        }
        return subjectNestedVoArrayList;
    }


    //判断二级分类是否重复
    private EduSubject existTwoSubject(String name,String pid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        return this.getOne(wrapper);
    }

    //判断一级分类是否重复
    private EduSubject existOneSubject(String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        return this.getOne(wrapper);
    }
}
