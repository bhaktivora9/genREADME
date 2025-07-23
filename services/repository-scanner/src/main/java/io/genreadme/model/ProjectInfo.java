package io.genreadme.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project information model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo {
    
    private String name;
    private String description;
    private String url;
    private String language;
    private String techStack;
    private String type; // Microservice, Web Application, Library, etc.
    private double complexity; // 1-10 scale
    private double quality; // 1-10 scale

    // Helper methods
    public boolean isSpringBootProject() {
        return techStack != null && techStack.toLowerCase().contains("spring boot");
    }

    public boolean isReactProject() {
        return techStack != null && techStack.toLowerCase().contains("react");
    }

    public boolean isPythonProject() {
        return language != null && language.equalsIgnoreCase("python") ||
               techStack != null && techStack.toLowerCase().contains("python");
    }

    public boolean isAIProject() {
        return techStack != null && (
                techStack.toLowerCase().contains("ai") ||
                techStack.toLowerCase().contains("artificial intelligence")
        );
    }

    public boolean isMLProject() {
        return techStack != null && (
                techStack.toLowerCase().contains("ml") ||
                techStack.toLowerCase().contains("machine learning")
        );
    }

    public boolean isCloudNative() {
        return type != null && (type.contains("Microservice") || type.contains("API"));
    }

    public String getComplexityLevel() {
        if (complexity >= 8.0) return "High";
        if (complexity >= 6.0) return "Medium";
        return "Low";
    }
}
