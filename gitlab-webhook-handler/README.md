# Git Event Handler - Google Cloud Function Java Project with CI/CD

## Project Structure
```
git-event-handler/
├── .github/
│   └── workflows/
│       └── deploy.yml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/
│   │           └── genreadme/
│   │               └── GitEventHandler.java
│   └── test/
│       └── java/
│           └── io/
│               └── genreadme/
│                   └── GitEventHandlerTest.java
├── pom.xml
└── README.md
```

## Prerequisites

1. **Google Cloud Project**: Create a GCP project and enable Cloud Functions API
2. **Service Account**: Create a service account with Cloud Functions Admin role
3. **GitHub Repository**: Push this code to a GitHub repository
4. **GitHub Secrets**: Configure the following secrets in your GitHub repository:
   - `GCP_PROJECT_ID`: Your Google Cloud Project ID
   - `GCP_SA_KEY`: Service account JSON key (base64 encoded or raw JSON)

## Local Development

### Setup
1. Install Java 17 or higher
2. Install Maven 3.6 or higher
3. Clone the repository

### Run Locally
```bash
# Run tests
mvn test

# Start local development server
mvn function:run

# Test the function
curl "http://localhost:8080?name=Local"
curl -X POST -H "Content-Type: application/json" -d '{"name":"Local"}' http://localhost:8080
```

## API Endpoints

### GET Request
```bash
curl "https://your-function-url?name=YourName"
```

Response:
```json
{
  "message": "Hello, YourName!",
  "method": "GET",
  "timestamp": 1623456789000
}
```

### POST Request
```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"name":"YourName"}' \
  https://your-function-url
```

Response:
```json
{
  "message": "Hello, YourName!",
  "method": "POST",
  "timestamp": 1623456789000,
  "receivedData": "{\"name\":\"YourName\"}"
}
```

## CI/CD Pipeline

The GitHub Actions workflow includes:

1. **Test Stage**: Runs unit tests on every push/PR
2. **Build Stage**: Compiles and packages the application
3. **Deploy Dev**: Deploys to development environment (develop branch)
4. **Deploy Prod**: Deploys to production environment (main branch)

### Branch Strategy
- `develop` branch → Development environment
- `main` branch → Production environment
- Pull requests run tests only

### Environment Configuration
- Development: `git-event-handler-dev`
- Production: `git-event-handler`

## Google Cloud Setup

### 1. Enable APIs
```bash
gcloud services enable cloudfunctions.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

### 2. Create Service Account
```bash
gcloud iam service-accounts create github-actions \
    --display-name="GitHub Actions"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/cloudfunctions.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
    --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/iam.serviceAccountUser"

gcloud iam service-accounts keys create key.json \
    --iam-account=github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com
```

### 3. Configure GitHub Secrets
1. Go to your GitHub repository → Settings → Secrets and variables → Actions
2. Add the following secrets:
   - `GCP_PROJECT_ID`: Your Google Cloud Project ID
   - `GCP_SA_KEY`: Contents of the `key.json` file

## Manual Deployment

Deploy directly using gcloud:

```bash
# Deploy to development
gcloud functions deploy git-event-handler-dev \
  --entry-point=io.genreadme.GitEventHandler \
  --runtime=java17 \
  --trigger=http \
  --source=. \
  --region=us-central1 \
  --allow-unauthenticated

# Deploy to production
gcloud functions deploy git-event-handler \
  --entry-point=io.genreadme.GitEventHandler \
  --runtime=java17 \
  --trigger=http \
  --source=. \
  --region=us-central1 \
  --allow-unauthenticated
```

## Monitoring and Logs

View function logs:
```bash
gcloud functions logs read git-event-handler --region=us-central1
```

## Security Considerations

1. **Authentication**: The function allows unauthenticated access. For production, consider:
   ```bash
   # Remove --allow-unauthenticated flag and add authentication
   gcloud functions deploy git-event-handler \
     --entry-point=io.genreadme.GitEventHandler \
     --runtime=java17 \
     --trigger=http \
     --source=. \
     --region=us-central1
   ```

2. **CORS**: Currently allows all origins (`*`). Restrict in production:
   ```java
   response.appendHeader("Access-Control-Allow-Origin", "https://yourdomain.com");
   ```

3. **Rate Limiting**: Configure appropriate max-instances based on expected load

## Troubleshooting

### Common Issues

1. **Build Failures**: Check Java version and Maven configuration
2. **Deployment Failures**: Verify service account permissions
3. **Function Errors**: Check Cloud Functions logs in GCP Console

### Debug Commands
```bash
# Test locally with specific port
mvn function:run -Drun.port=8081

# Verbose Maven output
mvn clean test -X

# Check function status
gcloud functions describe git-event-handler --region=us-central1
```