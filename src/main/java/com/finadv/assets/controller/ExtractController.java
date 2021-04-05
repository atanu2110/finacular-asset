package com.finadv.assets.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.CAMSEmail;
import com.finadv.assets.entities.NSDLReponse;
import com.finadv.assets.entities.ZerodhaResponse;
import com.finadv.assets.service.CAMSService;
import com.finadv.assets.service.NSDLService;
import com.finadv.assets.service.SeleniumService;
import com.finadv.assets.service.ZerodhaService;

/**
 * @author atanu
 *
 */
@RestController
@RequestMapping(value = "/api/v1")
@CrossOrigin(origins = "*")
public class ExtractController {

	private CAMSService camsService;
	
	@Autowired
	private SeleniumService seleniumService;
	@Autowired
	private NSDLService nsdlService;
	@Autowired
	private ZerodhaService zerodhaService;

	@Autowired
	public void setCamsService(CAMSService camsService) {
		this.camsService = camsService;
	}

	@PostMapping(path = "/cams")
	public ResponseEntity<String> extractMFDataFromCAMSFile(@RequestParam("camsFile") MultipartFile camsFile,
			@RequestParam("password") String password, @RequestParam("userId") Long userId) {
		try {
			return ResponseEntity.ok(camsService.extractMFData(camsFile, password, userId));
		} catch (IOException e) {
			// log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * @param camsEmail in body
	 * @return
	 */
	@PostMapping(path = "/camsonline/email")
	public ResponseEntity<String> triggerCAMSEmail(@RequestBody CAMSEmail camsEmail) {
		seleniumService.triggerCAMSEmail(camsEmail.getEmail(), camsEmail.getUserId());

		return new ResponseEntity<>("Successfully Sent CAMS email !!", HttpStatus.OK);
	}

	
	@PostMapping(path = "/nsdl")
	public ResponseEntity<NSDLReponse> extractFromNSDL(@RequestParam("nsdlFile") MultipartFile nsdlFile,
			@RequestParam("password") String password, @RequestParam("userId") Long userId , @RequestParam("source") String source) {
		return ResponseEntity.ok(nsdlService.extractFromNSDL(nsdlFile, password, userId , source));
	}
	
	@PostMapping(path = "/zerodha")
	public ResponseEntity<ZerodhaResponse> extractFromZerodhaExcel(@RequestParam("excelFile") MultipartFile excelFile,
			@RequestParam("userId") Long userId , @RequestParam("source") String source) {
		return ResponseEntity.ok(zerodhaService.extractFromZerodhaExcel(excelFile, userId, source));
	}
}
