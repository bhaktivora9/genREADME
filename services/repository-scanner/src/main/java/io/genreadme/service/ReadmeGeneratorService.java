package io.genreadme.service;

import io.genreadme.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Core service for README generation orchestration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReadmeGeneratorService {
    
    private final GitLabScannerService gitLabService;
    private final VertexAIService vertexAIService;
    private final MCPService mcpService;
    
    public ReadmeResponse generateReadme(ReadmeRequest request) {
        try {
            log.info("Starting README generation for: {}", request.getRepositoryUrl());
            
            
            // Step 1: Analyze project
            
            ProjectInfo projectInfo = gitLabService.analyzeProject(request.getRepositoryUrl());
            log.info("Project analysis complete: {} ({})", projectInfo.getName(), projectInfo.getTechStack());
            
            // Step 2: Find similar projects via MCP
            var similarProjects = mcpService.findSimilarProjects(projectInfo);
            log.info("Found {} similar projects for context", similarProjects.size());
            
            // Step 3: Generate README with AI
            String readmeContent = vertexAIService.generateIntelligentReadme(
                projectInfo, 
                similarProjects, 
                request.getStyle()
            );
            
            // Step 4: Create merge request (if requested)
            String mergeRequestUrl = null;
            if (request.isCreateMergeRequest()) {
                mergeRequestUrl = gitLabService.createReadmeMergeRequest(
                    request.getRepositoryUrl(), 
                    readmeContent
                );
            }
            
            return ReadmeResponse.builder()
                .success(true)
                .readmeContent(readmeContent)
                .projectInfo(projectInfo)
                .similarProjects(similarProjects)
                .mergeRequestUrl(mergeRequestUrl)
                .build();
                
        } catch (Exception e) {
            log.error("Failed to generate README", e);
            return ReadmeResponse.builder()
                .success(false)
                .error(e.getMessage())
                .build();
        }
    }
}