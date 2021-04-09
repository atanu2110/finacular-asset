package com.finadv.assets.service;

import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLReponse;

public interface CDSLService {

	public NSDLReponse extractFromCDSL(MultipartFile cdslFile, String password, Long userId , String source);
}
