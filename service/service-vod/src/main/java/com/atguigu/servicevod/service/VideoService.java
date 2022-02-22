package com.atguigu.servicevod.service;

import com.atguigu.commonutils.vo.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    String uploadVideo(MultipartFile file);

    void removeVideo(String videoId);

    R videoList(Integer pageNumber, Integer pageSize);

    R videoInfo(String videoId);

}
