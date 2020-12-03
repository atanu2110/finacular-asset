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
import com.finadv.assets.service.CAMSService;
import com.finadv.assets.service.SeleniumService;

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
	
	@PostMapping(path = "/camsonline/email")
	public ResponseEntity<String> triggerCAMSEmail(@RequestBody CAMSEmail camsEmail) {
		seleniumService.triggerCAMSEmail(camsEmail.getEmail(), camsEmail.getPassword());

		return new ResponseEntity<>("Successfully Sent CAMS email !!", HttpStatus.OK);
	}

}
