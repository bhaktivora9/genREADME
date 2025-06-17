package io.genreadme.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class GitLabCommit {
    
    private String id;
    
    private String message;
    
    private String title;
    
    private String timestamp;
    
    private String url;
    
    private GitLabAuthor author;
    
    private List<String> added;
    
    private List<String> modified;
    
    private List<String> removed;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class GitLabAuthor {
        
        private String name;
        
        private String email;
    }
}


