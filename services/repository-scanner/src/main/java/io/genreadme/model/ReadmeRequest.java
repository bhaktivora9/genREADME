package io.genreadme.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for README generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadmeRequest {
    
    private String repositoryUrl;
    private String style; // technical, business, developer-friendly, marketing
    private boolean createMergeRequest;
    private String targetBranch;
    
    // Validation
    public boolean isValid() {
        return repositoryUrl != null && 
               !repositoryUrl.trim().isEmpty() &&
               (repositoryUrl.contains("gitlab.com") || repositoryUrl.contains("github.com"));
    }
}