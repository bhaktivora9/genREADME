package io.genreadme.template;


import java.util.List;
import java.util.Map;

/**
 * Comprehensive prompt template system for generating intelligent READMEs
 * Supports different project types, styles, and AI enhancement strategies
 */
public class ReadmePromptTemplates {
    
    // ========================================
    // 🎯 MASTER PROMPT BUILDER
    // ========================================
    
    public static String buildMasterPrompt(String projectUrl, String style, 
                                         String detectedLanguage, List<String> similarProjects) {
        
        String baseTemplate = getBaseTemplate();
        String styleInstructions = getStyleInstructions(style);
        String projectTypeGuidance = getProjectTypeGuidance(projectUrl, detectedLanguage);
        String similarProjectsContext = buildSimilarProjectsContext(similarProjects);
        
        return String.format(baseTemplate, 
                           projectUrl, 
                           style,
                           styleInstructions,
                           projectTypeGuidance,
                           similarProjectsContext);
    }
    
    // ========================================
    // 📋 BASE TEMPLATE
    // ========================================
    
    private static String getBaseTemplate() {
        return """
            You are an expert technical writer and software engineer. Analyze this project and generate a comprehensive, professional README.md.
            
            🎯 PROJECT TO ANALYZE: %s
            📝 STYLE REQUESTED: %s
            
            %s
            
            %s
            
            %s
            
            📋 RESPONSE FORMAT:
            Return ONLY valid JSON in this exact structure:
            {
              "detectedTechnology": "Detected project type here",
              "confidence": 0.95,
              "similarProjects": [
                "owner/repo - Description with stars",
                "owner/repo - Description with stars",
                "owner/repo - Description with stars"
              ],
              "readmeContent": "# Project Title\\n\\n## 🚀 Description\\n\\nComprehensive project description here...\\n\\n## 📦 Installation\\n\\n```bash\\n# Installation commands\\n```\\n\\n## 🔧 Usage\\n\\n```javascript\\n// Usage examples\\n```\\n\\n## 🤝 Contributing\\n\\nContribution guidelines here..."
            }
            
            🚨 CRITICAL: Return ONLY the JSON, no markdown code blocks, no additional text.
            """;
    }
    
    // ========================================
    // 🎨 STYLE-SPECIFIC INSTRUCTIONS
    // ========================================
    
    private static String getStyleInstructions(String style) {
        Map<String, String> styleMap = Map.of(
            "professional", getProfessionalStyle(),
            "developer", getDeveloperStyle(),
            "minimal", getMinimalStyle(),
            "creative", getCreativeStyle(),
            "enterprise", getEnterpriseStyle()
        );
        
        return "🎨 STYLE INSTRUCTIONS:\n" + 
               styleMap.getOrDefault(style.toLowerCase(), getProfessionalStyle());
    }
    
    private static String getProfessionalStyle() {
        return """
            - Use clear, business-appropriate language
            - Include comprehensive sections: Description, Features, Installation, Usage, API Documentation
            - Add badges for build status, version, license
            - Professional tone throughout
            - Detailed contributing guidelines
            - Include troubleshooting section
            - Add deployment instructions
            - Use structured formatting with consistent headers
            """;
    }
    
    private static String getDeveloperStyle() {
        return """
            - Technical focus with code examples
            - Include architecture diagrams descriptions
            - Detailed API documentation
            - Code snippets for quick start
            - Development setup instructions
            - Testing guidelines
            - Performance considerations
            - Technical stack explanation
            - Advanced configuration options
            """;
    }
    
    private static String getMinimalStyle() {
        return """
            - Concise and to the point
            - Essential sections only: Description, Installation, Usage
            - Brief but clear explanations
            - Minimal but effective code examples
            - Clean, uncluttered formatting
            - Focus on getting started quickly
            - No unnecessary details
            """;
    }
    
    private static String getCreativeStyle() {
        return """
            - Use emojis and visual elements
            - Engaging, friendly tone
            - Creative section headers
            - Visual ASCII art or diagrams
            - Fun examples and use cases
            - Community-focused content
            - Encouraging and welcoming language
            - Interactive elements where appropriate
            """;
    }
    
    private static String getEnterpriseStyle() {
        return """
            - Enterprise-grade documentation standards
            - Compliance and security considerations
            - Detailed deployment architectures
            - Integration guidelines
            - Support and maintenance information
            - SLA and performance metrics
            - Enterprise feature highlights
            - Governance and policy alignment
            """;
    }
    
    // ========================================
    // 🔍 PROJECT TYPE GUIDANCE
    // ========================================
    
    private static String getProjectTypeGuidance(String projectUrl, String detectedLanguage) {
        String guidance = "🔍 PROJECT TYPE DETECTION:\n";
        guidance += "Analyze the project URL and detect the technology stack. ";
        guidance += "Look for these patterns:\n\n";
        
        guidance += """
            📱 FRONTEND PROJECTS:
            - React: Include component structure, state management, routing
            - Vue.js: Explain component architecture, Vue CLI usage
            - Angular: Detail modules, services, dependency injection
            - Flutter: Mobile-specific setup, platform considerations
            
            🖥️ BACKEND PROJECTS:
            - Spring Boot: Microservices, REST APIs, database integration
            - Node.js/Express: API development, middleware, async patterns
            - Django/Flask: Web framework features, ORM, authentication
            - .NET Core: Enterprise patterns, dependency injection, middleware
            
            📊 DATA & ML PROJECTS:
            - Python ML: Model training, data preprocessing, deployment
            - Jupyter Notebooks: Research methodology, reproducibility
            - R Projects: Statistical analysis, visualization packages
            
            🛠️ DEVOPS & INFRASTRUCTURE:
            - Docker: Containerization, multi-stage builds
            - Kubernetes: Deployment manifests, service mesh
            - Terraform: Infrastructure as code, provider configuration
            
            📚 LIBRARIES & FRAMEWORKS:
            - Focus on API documentation, examples, integration guides
            - Include installation methods (npm, pip, maven, etc.)
            - Provide usage patterns and best practices
            """;
        
        if (detectedLanguage != null && !detectedLanguage.isEmpty()) {
            guidance += String.format("\n🎯 DETECTED LANGUAGE: %s - Tailor content accordingly.", detectedLanguage);
        }
        
        return guidance;
    }
    
    // ========================================
    // 🔗 SIMILAR PROJECTS CONTEXT
    // ========================================
    
    private static String buildSimilarProjectsContext(List<String> similarProjects) {
        if (similarProjects == null || similarProjects.isEmpty()) {
            return """
                🔗 ECOSYSTEM ANALYSIS:
                Based on the detected project type, find 3-5 similar successful projects in the same ecosystem.
                Include popular libraries, frameworks, or tools that developers in this space commonly use.
                Format as: "owner/repo - Brief description (star count)"
                """;
        }
        
        String context = "🔗 ECOSYSTEM CONTEXT:\n";
        context += "Use these similar projects as inspiration for best practices:\n\n";
        
        for (String project : similarProjects) {
            context += "• " + project + "\n";
        }
        
        context += "\nLeverage patterns and approaches from these successful projects. ";
        context += "Reference similar architecture, documentation style, or feature sets where appropriate.";
        
        return context;
    }
    
    // ========================================
    // 🎯 SPECIALIZED PROMPTS
    // ========================================
    
    public static String buildWebhookPrompt(String projectUrl, String gitPlatform) {
        return String.format("""
            Generate a README for an automated webhook update.
            
            Project: %s
            Platform: %s
            
            🤖 AUTOMATION CONTEXT:
            - This is an automated README generation triggered by code changes
            - Focus on practical, immediately useful content
            - Assume the project is actively developed
            - Include CI/CD and development workflow information
            - Mention automated documentation updates
            
            Use developer-friendly style with:
            - Quick start instructions
            - Development setup
            - Contribution workflow
            - Automated testing information
            
            Return the same JSON format as above.
            """, projectUrl, gitPlatform);
    }
    
    public static String buildUpdatePrompt(String existingReadme, String projectUrl, 
                                         List<String> recentChanges) {
        String changesContext = recentChanges != null ? 
            String.join(", ", recentChanges) : "Recent code updates";
            
        return String.format("""
            Update an existing README based on recent project changes.
            
            Project: %s
            Recent Changes: %s
            
            EXISTING README:
            %s
            
            🔄 UPDATE INSTRUCTIONS:
            - Preserve the existing structure and tone
            - Update relevant sections based on recent changes
            - Add new features or capabilities mentioned in changes
            - Ensure consistency with existing content
            - Maintain all existing badges and links
            - Update version numbers or dependencies if mentioned
            
            Return the same JSON format with updated content.
            """, projectUrl, changesContext, existingReadme);
    }
    
    // ========================================
    // 🌐 LANGUAGE-SPECIFIC TEMPLATES
    // ========================================
    
    public static String getLanguageSpecificSections(String language) {
        Map<String, String> languageSections = Map.of(
            "java", getJavaSections(),
            "javascript", getJavaScriptSections(),
            "python", getPythonSections(),
            "csharp", getCSharpSections(),
            "go", getGoSections(),
            "rust", getRustSections()
        );
        
        return languageSections.getOrDefault(language.toLowerCase(), getGenericSections());
    }
    
    private static String getJavaSections() {
        return """
            ## 📋 Prerequisites
            - Java 17 or higher
            - Maven 3.6+ or Gradle 7+
            
            ## 🚀 Quick Start
            ```bash
            # Clone and build
            git clone [repo-url]
            cd [project-name]
            mvn clean install
            
            # Run the application
            mvn spring-boot:run
            ```
            
            ## 🔧 Configuration
            Configure application properties in `src/main/resources/application.yml`
            
            ## 🧪 Testing
            ```bash
            mvn test
            mvn verify
            ```
            """;
    }
    
    private static String getJavaScriptSections() {
        return """
            ## 📋 Prerequisites
            - Node.js 18+ and npm/yarn
            
            ## 🚀 Quick Start
            ```bash
            # Install dependencies
            npm install
            
            # Start development server
            npm run dev
            
            # Build for production
            npm run build
            ```
            
            ## 🧪 Testing
            ```bash
            npm test
            npm run test:coverage
            ```
            """;
    }
    
    private static String getPythonSections() {
        return """
            ## 📋 Prerequisites
            - Python 3.8+
            - pip or poetry
            
            ## 🚀 Quick Start
            ```bash
            # Create virtual environment
            python -m venv venv
            source venv/bin/activate  # Windows: venv\\Scripts\\activate
            
            # Install dependencies
            pip install -r requirements.txt
            
            # Run the application
            python main.py
            ```
            
            ## 🧪 Testing
            ```bash
            pytest
            pytest --cov
            ```
            """;
    }
    
    private static String getCSharpSections() {
        return """
            ## 📋 Prerequisites
            - .NET 6.0 or higher
            
            ## 🚀 Quick Start
            ```bash
            # Restore packages
            dotnet restore
            
            # Build the project
            dotnet build
            
            # Run the application
            dotnet run
            ```
            
            ## 🧪 Testing
            ```bash
            dotnet test
            ```
            """;
    }
    
    private static String getGoSections() {
        return """
            ## 📋 Prerequisites
            - Go 1.19+
            
            ## 🚀 Quick Start
            ```bash
            # Clone and run
            git clone [repo-url]
            cd [project-name]
            go mod download
            go run main.go
            ```
            
            ## 🧪 Testing
            ```bash
            go test ./...
            go test -race ./...
            ```
            """;
    }
    
    private static String getRustSections() {
        return """
            ## 📋 Prerequisites
            - Rust 1.70+
            
            ## 🚀 Quick Start
            ```bash
            # Clone and build
            git clone [repo-url]
            cd [project-name]
            cargo build
            
            # Run the application
            cargo run
            ```
            
            ## 🧪 Testing
            ```bash
            cargo test
            cargo test --release
            ```
            """;
    }
    
    private static String getGenericSections() {
        return """
            ## 📋 Prerequisites
            - [Runtime/SDK requirements]
            
            ## 🚀 Quick Start
            ```bash
            # Installation and setup commands
            ```
            
            ## 🧪 Testing
            ```bash
            # Testing commands
            ```
            """;
    }
    
    // ========================================
    // 🎨 TEMPLATE UTILITIES
    // ========================================
    
    public static String addBadges(String projectUrl, String language, String license) {
        StringBuilder badges = new StringBuilder();
        
        // Extract owner/repo from URL
        String[] urlParts = projectUrl.replace("https://", "").split("/");
        if (urlParts.length >= 3) {
            String owner = urlParts[1];
            String repo = urlParts[2].replace(".git", "");
            
            badges.append("[![Build Status](https://img.shields.io/github/workflow/status/")
                   .append(owner).append("/").append(repo).append("/CI)]\n");
            
            badges.append("[![Version](https://img.shields.io/github/v/release/")
                   .append(owner).append("/").append(repo).append(")]\n");
            
            badges.append("[![License](https://img.shields.io/github/license/")
                   .append(owner).append("/").append(repo).append(")]\n");
            
            if (language != null) {
                badges.append("[![Language](https://img.shields.io/github/languages/top/")
                       .append(owner).append("/").append(repo).append(")]\n");
            }
        }
        
        return badges.toString();
    }
    
    public static String buildContributingSection(String style) {
        if ("minimal".equals(style)) {
            return """
                ## 🤝 Contributing
                Pull requests are welcome. For major changes, please open an issue first.
                """;
        }
        
        return """
            ## 🤝 Contributing
            
            We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.
            
            ### Development Process
            1. Fork the repository
            2. Create a feature branch (`git checkout -b feature/amazing-feature`)
            3. Make your changes
            4. Add tests for your changes
            5. Ensure all tests pass
            6. Commit your changes (`git commit -m 'Add amazing feature'`)
            7. Push to the branch (`git push origin feature/amazing-feature`)
            8. Open a Pull Request
            
            ### Code Style
            - Follow the existing code style
            - Add tests for new features
            - Update documentation as needed
            - Ensure all CI checks pass
            """;
    }
}