# GenREADME: Automated README Generation 

## Overview

`genreadme` is a Java-based application designed to automate the generation and updates of `README.md` files within GitLab projects. By integrating with GitLab webhooks, `genreadme` listens for specific project events (e.g., push events, tag creations) and dynamically updates or creates the `README.md` based on predefined logic or templates. This project aims to streamline documentation maintenance, ensuring your project's `README.md` stays current with minimal manual intervention.

## Features

*   **GitLab Webhook Integration:** Seamlessly integrates with GitLab to receive real-time project event notifications.
*   **Automated README Generation/Update:** Triggers the creation or modification of `README.md` files upon configured GitLab events.
*   **Event-Driven:** Configurable to respond to various GitLab events (e.g., `Push events`, `Tag push events`, `Merge request events`).
*   **Java-based & Maven Managed:** Built using Java and managed with Apache Maven, ensuring a standard, robust, and portable build process.
*   **Extensible Design:** Provides a foundation for implementing custom README generation logic based on project details, commit history, or other metadata.

## Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 11 or higher:** `genreadme` is built with Java.
    *   [Download JDK](https://www.oracle.com/java/technologies/downloads/)
*   **Apache Maven 3.x:** Used for building the project and managing dependencies.
    *   [Download Maven](https://maven.apache.org/download.cgi)
*   **A GitLab Account:** Required for setting up webhooks and testing the integration.
*   **Internet Access:** For Maven to download dependencies and for your deployed application to receive webhooks from GitLab.

## Installation

Follow these steps to set up `genreadme` locally:

1.  **Clone the repository:**
    ```bash
    git clone https://gitlab.com/bhaktivora09/genreadme.git
    cd genreadme
    ```

2.  **Build the project with Maven:**
    This command compiles the source code, runs tests, and packages the application into a JAR file.
    ```bash
    mvn clean install
    ```

## Usage

### Running Locally (for Development and Testing)

You can run `genreadme` locally to test its functionality before deployment. This is particularly useful for debugging webhook payloads.

1.  **Start the local application:**
    ```bash
    mvn function:run
    ```
    This command will typically start a local web server (e.g., on `http://localhost:8080`) that listens for incoming HTTP requests, simulating a webhook endpoint.

2.  **Simulate a GitLab Webhook:**
    While `genreadme` awaits real webhooks, you can simulate one using `curl`. Replace the payload with a relevant GitLab event JSON (e.g., a push event).

    ```bash
    curl -X POST \
      -H "Content-Type: application/json" \
      -H "X-Gitlab-Event: Push Hook" \
      -d '{
        "object_kind": "push",
        "event_name": "push",
        "before": "a67f0da...",
        "after": "9e2c6e6...",
        "ref": "refs/heads/main",
        "checkout_sha": "9e2c6e6...",
        "message": null,
        "user_id": 123,
        "user_name": "John Doe",
        "user_email": "john.doe@example.com",
        "project_id": 456,
        "project": {
          "id": 456,
          "name": "MyProject",
          "description": "",
          "web_url": "http://gitlab.com/john.doe/myproject",
          "avatar_url": null,
          "git_ssh_url": "git@gitlab.com:john.doe/myproject.git",
          "git_http_url": "http://gitlab.com/john.doe/myproject.git",
          "namespace": "john.doe",
          "visibility_level": 0,
          "path_with_namespace": "john.doe/myproject",
          "default_branch": "main",
          "ci_config_path": null,
          "homepage": "http://gitlab.com/john.doe/myproject",
          "url": "git@gitlab.com:john.doe/myproject.git",
          "ssh_url": "git@gitlab.com:john.doe/myproject.git",
          "http_url": "http://gitlab.com/john.doe/myproject.git"
        },
        "commits": [
          {
            "id": "9e2c6e6...",
            "message": "Update README with new feature details",
            "timestamp": "2023-10-27T10:00:00+00:00",
            "url": "http://gitlab.com/john.doe/myproject/-/commit/9e2c6e6...",
            "author": {
              "name": "John Doe",
              "email": "john.doe@example.com"
            },
            "added": ["README.md"],
            "modified": [],
            "removed": []
          }
        ],
        "total_commits_count": 1,
        "repository": {
          "name": "MyProject",
          "url": "git@gitlab.com:john.doe/myproject.git",
          "description": "",
          "homepage": "http://gitlab.com/john.doe/myproject",
          "git_http_url": "http://gitlab.com/john.doe/myproject.git",
          "git_ssh_url": "git@gitlab.com:john.doe/myproject.git",
          "visibility_level": 0
        }
      }' \
      http://localhost:8080/function # Adjust port if different
    ```
    Observe the console output of `mvn function:run` for logging responses from the application.

### GitLab Webhook Integration

For `genreadme` to function as intended, you need to deploy it (e.g., on a server, a serverless platform like AWS Lambda or Google Cloud Functions) and configure a webhook in your GitLab project.

1.  **Deploy `genreadme`:** Deploy the compiled JAR file (`target/genreadme-1.0-SNAPSHOT.jar`) to an environment where it can receive HTTP requests from GitLab. The deployment method will depend on your infrastructure (e.g., a simple Spring Boot application run on a VM, a containerized application, or a serverless function).

2.  **Configure GitLab Webhook:**
    *   Navigate to your GitLab project.
    *   Go to **Settings > Webhooks**.
    *   **URL:** Enter the public URL of your
        deployed `genreadme` application's webhook endpoint (e.g., `https://your-deployed-service.com/function`).
    *   **Secret Token (Optional but Recommended):** If your `genreadme` implementation utilizes a secret token for payload verification, enter it here. This token helps ensure the webhook requests originate from GitLab.
    *   **Trigger:** Select the events that should trigger `genreadme`. Common choices for README updates include:
        *   `Push events` (for code changes to branches)
        *   `Tag push events` (for new versions/releases)
        *   You might also consider `Pipeline events` or `Merge request events` depending on your specific README update logic.
    *   **Enable SSL verification:** Keep this checked for production environments.
    *   Click **Add webhook**.

3.  **Test the Webhook:**
    After adding the webhook, GitLab provides a "Test" button. Use this to send a test payload and verify that `genreadme` receives and processes it correctly. Check the logs of your deployed `genreadme` instance for confirmation.

## Testing

To run the unit and integration tests defined in the project:

```bash
mvn test
This command will execute all tests located in the src/test directory, ensuring the codebase functions as expected.

Configuration
Currently, genreadme's primary logic resides within the Function.java file in this MVP branch. For more advanced features and production deployments, you would typically externalize configuration for:

GitLab API Token: To interact with the GitLab API (e.g., to commit changes to README.md). This should always be stored securely, ideally via environment variables or a secrets management service.
README Template Paths: If genreadme uses templates to generate READMEs.
Target File Name: The name of the file to generate/update (e.g., README.md).
Specific Branch/Repository Details: If the application needs to target specific branches or repositories.
These configurations can be managed using:

Environment Variables: Highly recommended for sensitive data like API tokens.
Application Properties Files: (application.properties or application.yml in a Spring Boot context) for non-sensitive configurations.
Example of setting an environment variable (for development/testing):

export GITLAB_PRIVATE_TOKEN="your_gitlab_personal_access_token"
Contributing
We welcome contributions to genreadme! If you have suggestions for improvements, new features, or bug fixes, please follow these steps:

Fork the repository on GitLab.
Clone your forked repository.
Create a new branch for your feature or bug fix:
git checkout -b feature/your-feature-name-or-bugfix/descriptive-name
Make your changes and ensure your code adheres to the project's coding standards.
Write or update tests for your changes.
Run mvn test to ensure all tests pass.
Commit your changes with a clear and concise commit message.
Push your branch to your forked repository.
Open a Merge Request (MR) to the main branch of the original genreadme repository.
Please provide a detailed description of your changes and why they are necessary.
For more detailed guidelines, please refer to the CONTRIBUTING.md file (if available in future iterations).

License
This project is licensed under the [Your Chosen License] - see the LICENSE.md file for details.
