package io.genreadme.service;

import io.genreadme.model.ProjectInfo;
import io.genreadme.model.SimilarProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MCP (Model Context Protocol) Service for ecosystem intelligence
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MCPService {

    private final VertexAIService vertexAIService;

    public List<SimilarProject> findSimilarProjects(ProjectInfo projectInfo) {
        try {
        	String prompt = buildSimilarProjectsPrompt(projectInfo);
        	String aiResponse = vertexAIService.ask(prompt);
        	List<SimilarProject> projects = parseSimilarProjects(aiResponse);

            // Score and return top 3
            projects.forEach(p -> p.setRelevanceScore(calculateRelevance(projectInfo, p)));

            return projects.stream()
                    .sorted(Comparator.comparingDouble(SimilarProject::getRelevanceScore).reversed())
                    .limit(3)
                    .toList();

        } catch (Exception e) {
            log.error("AI suggestion failed. Falling back to curated data.", e);
            return getFallbackProjects(projectInfo);
        }
    }

    public String buildSimilarProjectsPrompt(ProjectInfo projectInfo) {
        return String.format("""
            Suggest 3 high-quality open-source projects similar to the following:

            Name: %s
            Description: %s
            Tech Stack: %s
            Type: %s

            For each, provide:
            - Name
            - Description
            - GitHub URL
            - Stars
            - Quality Score (1–10)
            - Tech Stack
            - Project Type
            Format as plain text.
        """, projectInfo.getName(), projectInfo.getDescription(), projectInfo.getTechStack(), projectInfo.getType());
    }

    private List<SimilarProject> parseSimilarProjects(String response) {
        List<SimilarProject> projects = new ArrayList<>();

        if (response == null || response.trim().isEmpty()) return projects;

        // Split into entries
        String[] entries = response.split("\\n\\s*(?=\\d+\\.\\s|\\*\\*|Name:)");

        for (String entry : entries) {
            try {
                String name = extract(entry, "Name:\\s*(.*?)\\n", "\\*\\*(.*?)\\*\\*");
                String description = extract(entry, "Description:\\s*(.*?)\\n");
                String url = extract(entry, "URL:\\s*(.*?)\\n");
                int stars = Integer.parseInt(extract(entry, "Stars:\\s*(\\d+)"));
                double quality = Double.parseDouble(extract(entry, "Quality Score:\\s*([\\d.]+)"));
                String techStack = extract(entry, "Tech Stack:\\s*(.*?)\\n?");
                String type = extract(entry, "Type:\\s*(.*?)\\n?");

                projects.add(SimilarProject.builder()
                        .name(name)
                        .description(description)
                        .url(url)
                        .stars(stars)
                        .qualityScore(quality)
                        .techStack(techStack)
                        .projectType(type)
                        .build()
                );
            } catch (Exception e) {
                log.warn("Skipping malformed AI entry:\n{}\nError: {}", entry.trim(), e.getMessage());
            }
        }

        return projects;
    }

    private String extract(String text, String... patterns) {
        for (String pattern : patterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(text);
            if (m.find()) return m.group(1).trim();
        }
        throw new IllegalArgumentException("Field not found using given patterns.");
    }

    private List<SimilarProject> getFallbackProjects(ProjectInfo projectInfo) {
        List<SimilarProject> projects = new ArrayList<>();

        String stack = projectInfo.getTechStack().toLowerCase();
        if (stack.contains("spring boot")) projects.addAll(getSpringBootProjects());
        else if (stack.contains("react")) projects.addAll(getReactProjects());
        else if (stack.contains("python")) projects.addAll(getPythonProjects());

        projects.addAll(getGeneralProjects());

        projects.forEach(p -> p.setRelevanceScore(calculateRelevance(projectInfo, p)));

        return projects.stream()
                .sorted(Comparator.comparingDouble(SimilarProject::getRelevanceScore).reversed())
                .limit(3)
                .toList();
    }

    private double calculateRelevance(ProjectInfo target, SimilarProject candidate) {
        double score = 0.0;

        // Tech stack similarity
        if (target.getTechStack().toLowerCase()
                .contains(candidate.getTechStack().split(",")[0].trim().toLowerCase())) {
            score += 0.4;
        }

        // Project type match
        if (target.getType() != null && target.getType().equalsIgnoreCase(candidate.getProjectType())) {
            score += 0.3;
        }

        // Quality score (0–10 scaled)
        score += (candidate.getQualityScore() / 10.0) * 0.2;

        // Popularity
        score += Math.min(candidate.getStars() / 1000.0, 1.0) * 0.1;

        return score;
    }

    // --- Fallback Curated Lists ---

    private List<SimilarProject> getSpringBootProjects() {
        return List.of(
                SimilarProject.builder().name("spring-kafka-streams")
                        .description("High-performance Kafka streaming service with Spring Boot")
                        .url("https://github.com/example/spring-kafka-streams").stars(1240).qualityScore(9.2)
                        .techStack("Spring Boot, Kafka, PostgreSQL").projectType("Microservice").build(),

                SimilarProject.builder().name("microservice-gateway")
                        .description("API Gateway with Spring Cloud and service discovery")
                        .url("https://github.com/example/microservice-gateway").stars(856).qualityScore(8.7)
                        .techStack("Spring Boot, Spring Cloud, Redis").projectType("API Gateway").build(),

                SimilarProject.builder().name("reactive-api-service")
                        .description("WebFlux reactive microservice with R2DBC")
                        .url("https://github.com/example/reactive-api-service").stars(634).qualityScore(8.3)
                        .techStack("Spring WebFlux, R2DBC, PostgreSQL").projectType("Reactive Service").build());
    }

    private List<SimilarProject> getReactProjects() {
        return List.of(
                SimilarProject.builder().name("react-dashboard-pro")
                        .description("Modern dashboard with TypeScript and Material-UI")
                        .url("https://github.com/example/react-dashboard-pro").stars(2100).qualityScore(9.1)
                        .techStack("React, TypeScript, Material-UI").projectType("Dashboard").build(),

                SimilarProject.builder().name("nextjs-ecommerce")
                        .description("Full-stack e-commerce platform with Next.js")
                        .url("https://github.com/example/nextjs-ecommerce").stars(1450).qualityScore(8.9)
                        .techStack("Next.js, React, Stripe").projectType("E-commerce").build());
    }

    private List<SimilarProject> getPythonProjects() {
        return List.of(
                SimilarProject.builder().name("fastapi-ml-service")
                        .description("Machine learning API service with FastAPI")
                        .url("https://github.com/example/fastapi-ml-service").stars(980).qualityScore(8.8)
                        .techStack("FastAPI, Python, TensorFlow").projectType("ML Service").build(),

                SimilarProject.builder().name("django-rest-api")
                        .description("RESTful API with Django and PostgreSQL")
                        .url("https://github.com/example/django-rest-api").stars(756).qualityScore(8.4)
                        .techStack("Django, PostgreSQL, Redis").projectType("REST API").build());
    }

    private List<SimilarProject> getGeneralProjects() {
        return List.of(
                SimilarProject.builder().name("awesome-microservices")
                        .description("Collection of microservice patterns and best practices")
                        .url("https://github.com/example/awesome-microservices").stars(3200).qualityScore(9.5)
                        .techStack("Multi-language").projectType("Reference").build());
    }
}
