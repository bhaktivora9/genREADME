package io.genreadme.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Similar project model for MCP intelligence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarProject {
    
    private String name;
    private String description;
    private String url;
    private int stars;
    private double qualityScore; // 1-10 scale
    private String techStack;
    private String projectType;
    private double relevanceScore; // Calculated similarity to target project
    
    // Helper methods
    public String getPopularityLevel() {
        if (stars >= 1000) return "Very Popular";
        if (stars >= 500) return "Popular";
        if (stars >= 100) return "Well-Known";
        return "Emerging";
    }
    
    public boolean isHighQuality() {
        return qualityScore >= 8.0;
    }
}