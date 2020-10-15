package com.finadv.assets.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CAMSService {
    String extractMFData(MultipartFile camsFile, String password) throws IOException;
}
