package com.finadv.assets.service;

import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLReponse;
import com.finadv.assets.entities.ZerodhaResponse;

public interface ZerodhaService {

	public ZerodhaResponse extractFromZerodhaExcel(MultipartFile excelFile, Long userId, String source);
	
	public NSDLReponse portfolioAnalyzeFromZerodhaExcel(MultipartFile excelFile, Long userId, String source);
}
