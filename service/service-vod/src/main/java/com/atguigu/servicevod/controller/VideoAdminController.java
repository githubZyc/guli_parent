package com.atguigu.servicevod.controller;

import com.atguigu.commonutils.vo.R;
import com.atguigu.servicevod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api("阿里云视频点播微服务")
@RestController
@RequestMapping("/video")
public class VideoAdminController {
    @Autowired
    private VideoService videoService;

    @PostMapping("upload")
    public R uploadVideo(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {
        String videoId = videoService.uploadVideo(file);
        return R.ok().message("视频上传成功").data("videoId", videoId);
    }

    @DeleteMapping("{videoId}")
    public R removeVideo(@ApiParam(name = "videoId", value = "云端视频id", required = true)
        @PathVariable String videoId) {
        videoService.removeVideo(videoId);
        return R.ok().message("视频删除成功");
    }

    @GetMapping("videoList")
    public R videoList(@ApiParam(name = "pageNumber", value = "当前页码", required = true)
                         @RequestParam(defaultValue = "1") Integer pageNumber,
                       @ApiParam(name = "pageSize", value = "当前页码", required = true)
                       @RequestParam(defaultValue = "10")  Integer pageSize) {
        return videoService.videoList(pageNumber,pageSize);
    }

    @GetMapping("videoInfo")
    public R videoInfo(@ApiParam(name = "videoId", value = "当前页码", required = true)
                       @RequestParam(defaultValue = "1") String videoId) {
        return videoService.videoInfo(videoId);
    }
}
