package com.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SampleController serves the purpose to have sample REST API 
 * @author RV
 *
 */
@RestController
public class SampleController {
	
	Logger LOG = LoggerFactory.getLogger(SampleController.class);
	
	@GetMapping("/sample")
	public ResponseEntity<String> sample(){
		
		LOG.info("In SampleController - All Good!");
		
		return new ResponseEntity<String>("All Good!", HttpStatus.OK);
	}
}
