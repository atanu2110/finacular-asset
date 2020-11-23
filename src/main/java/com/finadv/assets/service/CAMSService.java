package com.finadv.assets.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CAMSService {

	String extractMFData(MultipartFile camsFile, String password, Long userId) throws IOException;
}
