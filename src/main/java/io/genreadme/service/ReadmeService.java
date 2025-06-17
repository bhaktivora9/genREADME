package io.genreadme.service;

import java.util.logging.Logger;

import io.genreadme.model.ReadmeRequest;
import io.genreadme.model.ReadmeResponse;

// ReadmeService.java - Just calls Vertex AI
public class ReadmeService {

	private static final Logger logger = Logger.getLogger(ReadmeService.class.getName());

	private final VertexAIService aiService = new VertexAIService();

	public ReadmeResponse generateReadme(ReadmeRequest request) {
		logger.info(" Generating intelligent README for: " + request.getProjectUrl());

		// ONE AI call does everything!
		ReadmeResponse response = aiService.generateReadmeWithIntelligence(request);

		logger.info("Generated README for " + response.getDetectedProjectType() + " with "
				+ response.getSimilarProjects().size() + " similar projects");

		return response;
	}
}