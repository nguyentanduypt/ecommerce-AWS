package com.tanduydev.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String folder);
    void deleteFile(String fileUrl);
}
