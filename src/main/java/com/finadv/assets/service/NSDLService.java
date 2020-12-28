package com.finadv.assets.service;

import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLReponse;

public interface NSDLService {

	public NSDLReponse extractFromNSDL(MultipartFile nsdlFile, String password, Long userId);
}
