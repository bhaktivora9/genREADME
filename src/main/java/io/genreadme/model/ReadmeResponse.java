package io.genreadme.model;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReadmeResponse {
    private String content;
    private String detectedProjectType;
    private List<String> similarProjects;
    private double aiConfidence;
    private long processingTimeMs;
    private String generatedAt = Instant.now().toString();
}