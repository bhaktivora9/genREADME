package io.genreadme.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;

import io.genreadme.model.ReadmeRequest;
import io.genreadme.model.ReadmeResponse;
import io.genreadme.template.ReadmePromptTemplates;

/**
 * Enhanced VertexAI service with comprehensive prompt templates
 * Supports multiple styles, project types, and intelligent README generation
 */
public class VertexAIService {
    
    private static final Logger logger = Logger.getLogger(VertexAIService.class.getName());
    
    private final String PROJECT_ID;
    private final String LOCATION;
    private final String MODEL_NAME;
    private final Gson gson;
    
    public VertexAIService() {
        this.PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
        this.LOCATION = System.getenv("GOOGLE_CLOUD_LOCATION") != null ? 
                        System.getenv("GOOGLE_CLOUD_LOCATION") : "us-central1";
        this.MODEL_NAME = "gemini-1.5-flash";
        this.gson = new Gson();
    }
    
    // ========================================
    // 🎯 MAIN GENERATION METHODS
    // ========================================
    
    /**
     * Generate README with full intelligence and template system
     */
    public ReadmeResponse generateReadmeWithIntelligence(ReadmeRequest request) {
        logger.info("🧠 Generating intelligent README for: " + request.getProjectUrl());
        
        try {
            // 1. Detect project characteristics
            String detectedLanguage = detectLanguageFromUrl(request.getProjectUrl());
            
            // 2. Get similar projects (mock data for now, real MCP later)
            List<String> similarProjects = getSimilarProjects(request.getProjectUrl(), detectedLanguage);
            
            // 3. Build comprehensive prompt using templates
            String prompt = ReadmePromptTemplates.buildMasterPrompt(
                request.getProjectUrl(),
                request.getStyle(),
                detectedLanguage,
                similarProjects
            );
            
            // 4. Call Vertex AI
            String aiResponse = callVertexAI(prompt);
            
            // 5. Parse and enhance response
            ReadmeResponse response = parseAIResponse(aiResponse);
            
            // 6. Post-process with language-specific sections
            response = enhanceWithLanguageSpecifics(response, detectedLanguage, request.getStyle());
            
            logger.info("✅ Generated README for " + response.getDetectedProjectType() + 
                       " (" + response.getContent().length() + " chars)");
            
            return response;
            
        } catch (Exception e) {
            logger.severe("Vertex AI failed: " + e.getMessage());
            return createIntelligentFallback(request);
        }
    }
    
    /**
     * Generate README for webhook automation
     */
    public ReadmeResponse generateForWebhook(String projectUrl, String gitPlatform) {
        logger.info("🤖 Generating automated README for webhook: " + projectUrl);
        
        try {
            String prompt = ReadmePromptTemplates.buildWebhookPrompt(projectUrl, gitPlatform);
            String aiResponse = callVertexAI(prompt);
            
            ReadmeResponse response = parseAIResponse(aiResponse);
			/*
			 * response.setAutomated(true); response.setTrigger("webhook");
			 */
            return response;
            
        } catch (Exception e) {
            logger.warning("Webhook README generation failed: " + e.getMessage());
            return createWebhookFallback(projectUrl, gitPlatform);
        }
    }
    
    /**
     * Update existing README based on changes
     */
    public ReadmeResponse updateExistingReadme(String existingReadme, String projectUrl, 
                                             List<String> recentChanges) {
        logger.info("🔄 Updating existing README for: " + projectUrl);
        
        try {
            String prompt = ReadmePromptTemplates.buildUpdatePrompt(existingReadme, projectUrl, recentChanges);
            String aiResponse = callVertexAI(prompt);
            
            ReadmeResponse response = parseAIResponse(aiResponse);
         //   response.setUpdateType("incremental");
            
            return response;
            
        } catch (Exception e) {
            logger.warning("README update failed: " + e.getMessage());
            return createUpdateFallback(existingReadme, projectUrl);
        }
    }
    
    // ========================================
    // 🤖 VERTEX AI INTEGRATION
    // ========================================
    
    private String callVertexAI(String prompt) throws Exception {
        logger.info("🧠 Calling Vertex AI (prompt length: " + prompt.length() + " chars)");
        
        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
            
            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);
            
            // Add generation config for better results
            GenerateContentResponse response = model.generateContent(prompt);
            
            String result = ResponseHandler.getText(response);
            
            logger.info("✅ Vertex AI response received (" + result.length() + " chars)");
            return result;
            
        } catch (Exception e) {
            logger.severe("Vertex AI call error: " + e.getMessage());
            throw new RuntimeException("Vertex AI service unavailable", e);
        }
    }
    
    // ========================================
    // 📊 RESPONSE PARSING & ENHANCEMENT
    // ========================================
    
    private ReadmeResponse parseAIResponse(String aiResponse) {
        try {
            // Clean the response (remove markdown code blocks if present)
            String cleanJson = cleanJsonResponse(aiResponse);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = gson.fromJson(cleanJson, Map.class);
            
            @SuppressWarnings("unchecked")
            List<String> similarProjects = (List<String>) parsed.get("similarProjects");
            
            return ReadmeResponse.builder()
                .content((String) parsed.get("readmeContent"))
                .detectedProjectType((String) parsed.get("detectedTechnology"))
                .similarProjects(similarProjects)
                .aiConfidence(((Number) parsed.get("confidence")).doubleValue())
                .processingTimeMs(System.currentTimeMillis())
                .generatedAt(java.time.Instant.now().toString())
                //.templateVersion("2.0")
                .build();
                
        } catch (Exception e) {
            logger.warning("Failed to parse AI JSON response: " + e.getMessage());
            logger.info("Raw response: " + aiResponse.substring(0, Math.min(500, aiResponse.length())));
            throw new RuntimeException("AI response parsing failed", e);
        }
    }
    
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        
        // Remove markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        // Find JSON start and end
        int jsonStart = cleaned.indexOf('{');
        int jsonEnd = cleaned.lastIndexOf('}');
        
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }
        
        return cleaned.trim();
    }
    
    private ReadmeResponse enhanceWithLanguageSpecifics(ReadmeResponse response, 
                                                      String language, String style) {
        if (language == null || response.getContent() == null) {
            return response;
        }
        
        try {
            String languageSpecificSections = ReadmePromptTemplates.getLanguageSpecificSections(language);
            String contributingSection = ReadmePromptTemplates.buildContributingSection(style);
            
            // Enhance the README content with language-specific sections
            String enhancedContent = response.getContent();
            
            // Add language-specific sections if not present
            if (!enhancedContent.contains("Prerequisites") && !enhancedContent.contains("Quick Start")) {
                enhancedContent += "\n\n" + languageSpecificSections;
            }
            
            // Add contributing section if not present
            if (!enhancedContent.toLowerCase().contains("contributing")) {
                enhancedContent += "\n\n" + contributingSection;
            }
            
            response.setContent(enhancedContent);
            //response.setEnhanced(true);
            
        } catch (Exception e) {
            logger.warning("Failed to enhance with language specifics: " + e.getMessage());
        }
        
        return response;
    }
    
    // ========================================
    // 🔍 INTELLIGENT DETECTION
    // ========================================
    
    private String detectLanguageFromUrl(String projectUrl) {
        String url = projectUrl.toLowerCase();
        
        // Framework/technology detection
        if (url.contains("spring") || url.contains("java")) return "java";
        if (url.contains("react") || url.contains("next") || url.contains("vue")) return "javascript";
        if (url.contains("angular")) return "typescript";
        if (url.contains("django") || url.contains("flask") || url.contains("python")) return "python";
        if (url.contains("dotnet") || url.contains("csharp") || url.contains("aspnet")) return "csharp";
        if (url.contains("golang") || url.contains("/go-")) return "go";
        if (url.contains("rust") || url.contains("cargo")) return "rust";
        if (url.contains("flutter") || url.contains("dart")) return "dart";
        if (url.contains("kotlin")) return "kotlin";
        if (url.contains("swift")) return "swift";
        if (url.contains("ruby") || url.contains("rails")) return "ruby";
        if (url.contains("php") || url.contains("laravel")) return "php";
        
        return "generic";
    }
    
    private List<String> getSimilarProjects(String projectUrl, String language) {
        // This would integrate with your MCP service in the future
        // For now, return intelligent mock data based on detected language
        
        Map<String, List<String>> ecosystemData = Map.of(
            "java", List.of(
                "spring-projects/spring-boot - Production-ready Java framework (70k stars)",
                "Netflix/hystrix - Latency tolerance library (24k stars)",
                "google/guava - Core libraries for Java (49k stars)"
            ),
            "javascript", List.of(
                "vercel/next.js - React framework for production (120k stars)",
                "facebook/create-react-app - Set up React apps (102k stars)",
                "mui/material-ui - React components library (92k stars)"
            ),
            "python", List.of(
                "pallets/flask - Lightweight WSGI web framework (66k stars)",
                "django/django - High-level Python web framework (77k stars)",
                "psf/requests - HTTP library for humans (51k stars)"
            ),
            "generic", List.of(
                "github/gitignore - Useful .gitignore templates (159k stars)",
                "sindresorhus/awesome - Awesome lists about all kinds of topics (300k stars)",
                "microsoft/vscode - Source code editor (160k stars)"
            )
        );
        
        return ecosystemData.getOrDefault(language, ecosystemData.get("generic"));
    }
    
    // ========================================
    // 🛡️ FALLBACK MECHANISMS
    // ========================================
    
    private ReadmeResponse createIntelligentFallback(ReadmeRequest request) {
        logger.info("🛡️ Creating intelligent fallback README");
        
        String detectedLanguage = detectLanguageFromUrl(request.getProjectUrl());
        String languageSpecificSections = ReadmePromptTemplates.getLanguageSpecificSections(detectedLanguage);
        
        String fallbackReadme = String.format("""
            # %s
            
            %s
            
            ## 🚀 Description
            A %s project that demonstrates modern software development practices.
            
            %s
            
            ## 📄 License
            This project is licensed under the MIT License - see the LICENSE file for details.
            
            ## 🤝 Support
            For support, please open an issue or contact the maintainers.
            """, 
            extractProjectName(request.getProjectUrl()),
            ReadmePromptTemplates.addBadges(request.getProjectUrl(), detectedLanguage, "MIT"),
            detectedLanguage,
            languageSpecificSections
        );
        
        return ReadmeResponse.builder()
            .content(fallbackReadme)
            .detectedProjectType(capitalizeFirst(detectedLanguage) + " Project")
            .similarProjects(getSimilarProjects(request.getProjectUrl(), detectedLanguage))
            .aiConfidence(0.3)
            .processingTimeMs(100L)
         //   .fallback(true)
           // .templateVersion("fallback-1.0")
            .build();
    }
    
    private ReadmeResponse createWebhookFallback(String projectUrl, String gitPlatform) {
        String projectName = extractProjectName(projectUrl);
        
        String webhookReadme = String.format("""
            # %s
            
            🤖 **Automated README** - Last updated: %s
            
            ## 📋 Overview
            This README was automatically generated by GenREADME webhook integration.
            
            ## 🔄 Automated Updates
            - This documentation is automatically updated when code changes
            - Powered by %s webhooks
            - AI-generated content with ecosystem intelligence
            
            ## 🚀 Quick Start
            ```bash
            git clone %s
            cd %s
            # Follow setup instructions in the repository
            ```
            
            ## 📞 Support
            For questions about this project, please check the repository or contact the maintainers.
            """, 
            projectName,
            java.time.Instant.now(),
            gitPlatform,
            projectUrl,
            projectName.toLowerCase().replace(" ", "-")
        );
        
        return ReadmeResponse.builder()
            .content(webhookReadme)
            .detectedProjectType("Automated Project")
            .similarProjects(List.of("Webhook-generated projects"))
            .aiConfidence(0.5)
            .processingTimeMs(50L)
            //.automated(true)
            //.fallback(true)
            .build();
    }
    
    private ReadmeResponse createUpdateFallback(String existingReadme, String projectUrl) {
        String projectName = extractProjectName(projectUrl);
        
        String updatedReadme = existingReadme + "\n\n" + String.format("""
            ## 🔄 Recent Updates
            - Documentation updated: %s
            - AI-enhanced content based on project analysis
            - Automated improvements applied
            
            ---
            *This README was enhanced by GenREADME AI*
            """, 
            java.time.Instant.now()
        );
        
        return ReadmeResponse.builder()
            .content(updatedReadme)
            .detectedProjectType("Updated Project")
            .similarProjects(List.of("Updated documentation projects"))
            .aiConfidence(0.4)
            .processingTimeMs(75L)
       //     .updateType("fallback")
         //   .fallback(true)
            .build();
    }
    
    // ========================================
    // 🔧 UTILITY METHODS
    // ========================================
    
    private String extractProjectName(String projectUrl) {
        if (projectUrl == null || projectUrl.isEmpty()) {
            return "Unknown Project";
        }
        
        try {
            // Extract from GitHub/GitLab URL pattern
            String[] parts = projectUrl.split("/");
            if (parts.length >= 2) {
                String repoName = parts[parts.length - 1];
                // Remove .git extension if present
                if (repoName.endsWith(".git")) {
                    repoName = repoName.substring(0, repoName.length() - 4);
                }
                // Convert kebab-case to Title Case
                return Arrays.stream(repoName.split("-"))
                    .map(this::capitalizeFirst)
                    .collect(Collectors.joining(" "));
            }
        } catch (Exception e) {
            logger.warning("Failed to extract project name from URL: " + projectUrl);
        }
        
        return "Project";
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    // ========================================
    // 🧪 VALIDATION & HEALTH CHECKS
    // ========================================
    
    /**
     * Validate that the service is properly configured
     */
    public boolean isHealthy() {
        try {
            if (PROJECT_ID == null || PROJECT_ID.isEmpty()) {
                logger.severe("GOOGLE_CLOUD_PROJECT environment variable not set");
                return false;
            }
            
            // Test basic Vertex AI connectivity
            try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
                GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);
                // Simple test prompt
                GenerateContentResponse response = model.generateContent("Hello");
                String result = ResponseHandler.getText(response);
                
                logger.info("✅ Vertex AI health check passed");
                return result != null && !result.isEmpty();
                
            } catch (Exception e) {
                logger.severe("❌ Vertex AI health check failed: " + e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            logger.severe("Service health check failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get service configuration info
     */
    public Map<String, String> getServiceInfo() {
        return Map.of(
            "projectId", PROJECT_ID != null ? PROJECT_ID : "NOT_SET",
            "location", LOCATION,
            "modelName", MODEL_NAME,
            "status", isHealthy() ? "healthy" : "unhealthy",
            "version", "2.0.0"
        );
    }
}