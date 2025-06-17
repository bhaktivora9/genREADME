package io.genreadme.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReadmeRequest {
    private String projectUrl;
    private String style = "professional"; // professional, developer, minimal
    private boolean includeContributing = true;
    private boolean includeLicense = true;
    private boolean includeDeployment = false;
}