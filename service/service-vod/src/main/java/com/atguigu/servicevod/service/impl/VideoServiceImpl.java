package com.atguigu.servicevod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.*;
import com.atguigu.commonutils.vo.R;
import com.atguigu.servicebase.handler.exception.GuiGuException;
import com.atguigu.servicevod.config.ConstantProperties;
import com.atguigu.servicevod.service.VideoService;
import com.atguigu.servicevod.utils.AliYunVodSDKUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.atguigu.servicevod.utils.AliYunVodSDKUtils.initVodClient;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    ConstantProperties constantProperties;

    /**
     * 功能描述: 上传视频到阿里云
     * @Author ZYC
     * @Date 2022/2/16 11:46
     * @Param [file]
     * @Return java.lang.String
     * @Version 1.0
     **/
    @Override
    public String uploadVideo(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            UploadStreamRequest request = new UploadStreamRequest(
                    constantProperties.getKeyId(),
                    constantProperties.getKeySecret(),
                    title, originalFilename, inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                log.warn(errorMessage);
                if (StringUtils.isEmpty(videoId)) {
                    throw new GuiGuException(20001, errorMessage);
                }
            }
            return videoId;
        } catch (IOException e) {
            throw new GuiGuException(20001, "vod 服务上传失败");
        }
    }

    /**
     * 功能描述: 删除视频
     * @Author ZYC
     * @Date 2022/2/16 11:47
     * @Param [videoId]
     * @Return void
     * @Version 1.0
     **/
    @Override
    public void removeVideo(String videoId) {
        try{
            DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(constantProperties.getKeyId(),constantProperties.getKeySecret());
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoId);
            DeleteVideoResponse response = client.getAcsResponse(request);
            log.debug("RequestId = " + response.getRequestId() + "\n");
        }catch (Exception e){
            throw new GuiGuException(20001, "视频删除失败");
        }
    }

    /**
     * 功能描述: 已上传视频列表
     * @Author ZYC
     * @Date 2022/2/16 13:53
     * @Param [pageNumber, pageSize]
     * @Return java.util.List
     * @Version 1.0
     **/
    @Override
    public R videoList(Integer pageNumber, Integer pageSize) {
        try{
            DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(constantProperties.getKeyId(),constantProperties.getKeySecret());
            GetVideoListRequest getVideoListRequest = new GetVideoListRequest();
            getVideoListRequest.setPageNo(pageNumber);
            getVideoListRequest.setPageSize(pageSize);
            GetVideoListResponse acsResponse = client.getAcsResponse(getVideoListRequest);
            List<GetVideoListResponse.Video> videoList = acsResponse.getVideoList();
            return R.ok().data("total",acsResponse.getTotal()).data("item",videoList);
        }catch (Exception e){
            throw new GuiGuException(20001,"获取视频列表失败");
        }
    }

    /**
     * 功能描述: 获取视频详情
     * @Author ZYC
     * @Date 2022/2/16 14:02
     * @Param [videoId]
     * @Return com.atguigu.commonutils.vo.R
     * @Version 1.0
     **/
    @Override
    public R videoInfo(String videoId) {
        try{
            DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(constantProperties.getKeyId(),constantProperties.getKeySecret());
            GetVideoInfoRequest getVideoInfoRequest = new GetVideoInfoRequest();
            getVideoInfoRequest.setVideoId(videoId);
            GetVideoInfoResponse acsResponse = client.getAcsResponse(getVideoInfoRequest);
            GetVideoInfoResponse.Video video = acsResponse.getVideo();
            return R.ok().data("data",video);
        }catch (Exception e){
            throw new GuiGuException(20001,"获取视频详情失败");
        }
    }
}
