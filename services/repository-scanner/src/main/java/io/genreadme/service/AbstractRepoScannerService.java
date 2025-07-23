package io.genreadme.service;


import io.genreadme.model.ModuleMetadata;

import java.util.List;

public abstract class AbstractRepoScannerService {
    /**
     * Scans a repository for project modules, tech stack, and dependency info.
     * 
     * @param projectIdOrPath GitHub or GitLab project name/path/id
     * @return List of module metadata
     */
    public abstract List<ModuleMetadata> analyzeProjectStructure(String projectIdOrPath);
}
