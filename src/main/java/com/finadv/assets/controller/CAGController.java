package com.finadv.assets.controller;

import com.finadv.assets.service.impl.CAGService;
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
 * Controller for CAG File Data Extraction APIs.
 */
@RestController
@Slf4j
@Api(tags = "cag")
@RequestMapping(value = "/api/v1/cag")
public class CAGController {

    private final CAGService cagService;

    @Autowired
    public CAGController(CAGService cagService) {
        this.cagService = cagService;
    }


    /**
     * Endpoint to read CAG Details from CAG File.
     * @return
     */
    @ApiOperation(value = "Parse, Extract and process the CAG File for Details")
    @PostMapping(path = "/extract-cag")
    public ResponseEntity<String> extractDataFromCAGFile(@RequestParam("cag_file") MultipartFile cag_file, @RequestParam("password") String password) {
        try {
            return ResponseEntity.ok(cagService.extractCAGData(cag_file, password));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
