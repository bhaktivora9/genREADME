package io.genreadme.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for README generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadmeResponse {
    
    private boolean success;
    private String readmeContent;
    private String error;
    private ProjectInfo projectInfo;
    private List<SimilarProject> similarProjects;
    private String mergeRequestUrl;
    private long processingTimeMs;
}