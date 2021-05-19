package com.finadv.assets.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLReponse;

public interface CAMSService {

	String extractMFData(MultipartFile camsFile, String password, Long userId, String source) throws IOException;
	
	NSDLReponse portfolioAnalyzeCAMS(MultipartFile camsFile, String password, Long userId, String source) throws IOException;
}
