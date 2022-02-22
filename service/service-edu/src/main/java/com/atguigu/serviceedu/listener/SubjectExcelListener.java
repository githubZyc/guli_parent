package com.atguigu.serviceedu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.servicebase.constant.ResultCodeEnum;
import com.atguigu.servicebase.handler.exception.GuiGuException;
import com.atguigu.serviceedu.dto.excel.ExcelSubjectData;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class SubjectExcelListener extends AnalysisEventListener<ExcelSubjectData> {

    private List<ExcelSubjectData> excelData = new CopyOnWriteArrayList<>();

    //一行一行去读取excle内容
    @Override
    public void invoke(ExcelSubjectData t, AnalysisContext analysisContext) {
        if(t == null) {
            throw new GuiGuException(ResultCodeEnum.NO_DATA);
        }
        excelData.add(t);
    }
        //读取excel表头信息
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息："+headMap);
    }

    //读取完成后执行
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {}
}
