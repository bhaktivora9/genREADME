package io.genreadme.service;

import io.genreadme.model.GitLabWebhookEvent;
import io.genreadme.model.ReadmeRequest;
import io.genreadme.model.ReadmeResponse;

//WebhookProcessor.java - GitLab webhook business logic
public class WebhookProcessor {
 
 private final ReadmeService readmeService;
 
 public WebhookProcessor(ReadmeService readmeService) {
     this.readmeService = readmeService;
 }
 
 public String processWebhook(GitLabWebhookEvent event) {
     
     // Only process push events to main/master
     if (!shouldProcessEvent(event)) {
         return String.format("Skipped: %s event to %s branch", 
             event.getObjectKind(), getBranchName(event.getRef()));
     }
     
     try {
         // Create README request from webhook
         ReadmeRequest request = ReadmeRequest.builder()
             .projectUrl(event.getRepository().getHomepage())
             .style("developer") // Default style for auto-generation
             .includeContributing(true)
             .includeLicense(true)
             .build();
         
         // Generate README using same service as web API
         ReadmeResponse response = readmeService.generateReadme(request);
         
         // TODO: Create GitLab merge request (if you have time)
         // String mrUrl = createMergeRequest(event.getProjectId(), response.getContent());
         
         return String.format("✅ Generated %s README (%d chars) with %d similar projects analyzed", 
             response.getDetectedProjectType(),
             response.getContent().length(),
             response.getSimilarProjects().size()
         );
         
     } catch (Exception e) {
         return "❌ Failed to generate README: " + e.getMessage();
     }
 }
 
 private boolean shouldProcessEvent(GitLabWebhookEvent event) {
     // Only process push events
     if (!"push".equals(event.getObjectKind())) {
         return false;
     }
     
     // Only process main/master branches
     String branch = getBranchName(event.getRef());
     return "main".equals(branch) || "master".equals(branch);
 }
 
 private String getBranchName(String ref) {
     return ref != null ? ref.replace("refs/heads/", "") : "";
 }
}