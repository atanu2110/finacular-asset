package com.finadv.assets.controller;

import com.finadv.assets.service.CAMSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for CAMS File Data Extraction APIs.
 */
@RestController
@Slf4j
@Api(tags = "cams")
@RequestMapping(value = "/api/v1/cams")
public class CAMSController {

    private final CAMSService camsService;

    @Autowired
    public CAMSController(CAMSService camsService) {
        this.camsService = camsService;
    }


    /**
     * Endpoint to read Mutual Fund Details from CAMS File.
     * @return
     */
    @ApiOperation(value = "Parse, Extract and process the CAMS File for Mutual Fund Details of User")
    @PostMapping(path = "/extract-mf")
    public ResponseEntity<String> extractMFDataFromCAMSFile(@RequestParam("cams_file") MultipartFile camsFile, @RequestParam("password") String password) {
        try {
            return ResponseEntity.ok(camsService.extractMFData(camsFile, password));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
