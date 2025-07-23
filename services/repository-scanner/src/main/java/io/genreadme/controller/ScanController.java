package io.genreadme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 *   REST Controller for README generation
 */
@RestController
@Slf4j
public class ScanController {



	@GetMapping("/home")
	public ResponseEntity<String> homePage() {

		return ResponseEntity.ok("This GenREADME");

	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("GenREADME Main Service is healthy");
	}
}