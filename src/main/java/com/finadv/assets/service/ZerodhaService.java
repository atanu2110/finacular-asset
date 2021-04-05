package com.finadv.assets.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.finadv.assets.entities.ZerodhaResponse;

public interface ZerodhaService {

	public ZerodhaResponse extractFromZerodhaExcel(MultipartFile excelFile, Long userId, String source);
}
