package com.atguigu.serviceoss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 */
public interface FileService {
    /**
     * 文件上传至阿里云
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}
