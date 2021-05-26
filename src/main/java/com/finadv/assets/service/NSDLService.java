package com.finadv.assets.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLReponse;

public interface NSDLService {

	public NSDLReponse extractFromNSDL(MultipartFile nsdlFile, String password, Long userId , String source) throws IOException;
}
