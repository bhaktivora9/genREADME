package io.genreadme.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.genreadme.model.ProjectInfo;
import io.genreadme.model.SimilarProject;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VertexAIService {

	private final String projectId;
	private final String location;
	private final String modelName;

	public VertexAIService(@Value("${vertex.ai.project}") String projectId,
			@Value("${vertex.ai.location:us-central1}") String location,
			@Value("${vertex.ai.model:gemini-1.5-flash}") String modelName) {
		this.projectId = projectId;
		this.location = location;
		this.modelName = modelName;
	}

	public String generateIntelligentReadme(ProjectInfo projectInfo, List<SimilarProject> similarProjects,
			String style) {

		/*
		 * try (VertexAI vertexAi = new VertexAI(projectId, location)) { GenerativeModel
		 * model = new GenerativeModel(modelName, vertexAi);
		 * 
		 * String prompt = buildPrompt(projectInfo, similarProjects, style);
		 * log.debug("Prompt sent to Gemini:\n{}", prompt);
		 * 
		 * GenerateContentResponse response = model.generateContent(prompt); return
		 * response.getText();
		 * 
		 */
		try {
			return "";
		} /*
			 * catch (IOException e) { log.error("Failed to initialize VertexAI client", e);
			 * throw new RuntimeException("VertexAI client setup failed", e); }
			 */ catch (Exception e) {
			log.error("Gemini model call failed", e);
			return generateFallbackReadme(projectInfo);
		}
	}

	private String buildPrompt(ProjectInfo projectInfo, List<SimilarProject> similarProjects, String style) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Generate a professional and complete README file for the following project:\n\n")
				.append("Project Name: ").append(projectInfo.getName()).append("\n").append("Description: ")
				.append(projectInfo.getDescription()).append("\n").append("Tech Stack: ")
				.append(projectInfo.getTechStack()).append("\n").append("Type: ").append(projectInfo.getType())
				.append("\n").append("Complexity: ").append(projectInfo.getComplexity()).append("/10\n")
				.append("Quality: ").append(projectInfo.getQuality()).append("/10\n\n");

		if (!similarProjects.isEmpty()) {
			prompt.append("Here are some similar successful open-source projects for inspiration:\n");
			for (SimilarProject sp : similarProjects) {
				prompt.append("- ").append(sp.getName()).append(": ").append(sp.getDescription()).append(" (")
						.append(sp.getStars()).append(" stars)\n");
			}
		}

		prompt.append("\nREADME Style: ").append(style)
				.append("\nInclude sections like Features, Setup, Tech Stack, Contribution, and License.\n");

		return prompt.toString();
	}

	private String generateFallbackReadme(ProjectInfo projectInfo) {
		return String.format("""
				# %s

				A %s project built with %s.

				## Getting Started

				1. Clone this repository
				2. Install dependencies
				3. Run the application

				## Contributing

				Pull requests are welcome!

				## License

				MIT License
				""", projectInfo.getName(), projectInfo.getType().toLowerCase(), projectInfo.getTechStack());
	}

	public String ask(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}
}
