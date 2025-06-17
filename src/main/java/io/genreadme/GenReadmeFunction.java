package io.genreadme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Logger;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import io.genreadme.http.enums.ContentType;
import io.genreadme.model.GitLabWebhookEvent;
import io.genreadme.model.ReadmeRequest;
import io.genreadme.model.ReadmeResponse;
import io.genreadme.service.ReadmeService;
import io.genreadme.service.WebhookProcessor;
import io.genreadme.util.HttpResponseUtils;
import io.genreadme.util.RequestDetector;

//GenReadmeFunction.java - ONE function for both web API and webhooks
public class GenReadmeFunction implements HttpFunction {

	private static final Gson gson = new Gson();
	private static final Logger logger = Logger.getLogger(GenReadmeFunction.class.getName());

	// Services
	private final ReadmeService readmeService = new ReadmeService();
	private final WebhookProcessor webhookProcessor = new WebhookProcessor(readmeService);
	private final RequestDetector requestDetector = new RequestDetector();

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {

		// CORS headers for web requests
		HttpResponseUtils.configureCors(response);

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatusCode(200);
			return;
		}

		try {
			// Read request body
			String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

			logger.info("Received request: " + requestBody.substring(0, Math.min(200, requestBody.length())));

			// 🎯 DETECT REQUEST TYPE
			if (requestDetector.isGitLabWebhook(request, requestBody)) {
				handleWebhook(requestBody, response);
			} else {
				handleWebRequest(requestBody, response);
			}

		} catch (Exception e) {
			logger.severe("Error processing request: " + e.getMessage());
			sendErrorResponse(response, e.getMessage());
		}
	}

	// 🎯 HANDLE WEB API REQUESTS
	private void handleWebRequest(String requestBody, HttpResponse response) throws IOException {
		logger.info("Processing web API request");

		ReadmeRequest readmeRequest = gson.fromJson(requestBody, ReadmeRequest.class);
		ReadmeResponse readmeResponse = readmeService.generateReadme(readmeRequest);

		HttpResponseUtils.setHeader(response, "Content-Type", "application/json");
		response.getWriter().write(gson.toJson(readmeResponse));
		response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
	}

	// 🎯 HANDLE GITLAB WEBHOOKS
	private void handleWebhook(String requestBody, HttpResponse response) throws IOException {
		logger.info("Processing GitLab webhook");

		GitLabWebhookEvent webhookEvent = gson.fromJson(requestBody, GitLabWebhookEvent.class);
		String result = webhookProcessor.processWebhook(webhookEvent);

		HttpResponseUtils.setContentType(response, ContentType.APPLICATION_JSON);
		response.getWriter().write(gson.toJson(Map.of("status", "success", "message", result, "type", "webhook")));
		response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
	}

	private void sendErrorResponse(HttpResponse response, String error) throws IOException {
		response.getWriter().write(gson.toJson(Map.of("error", error)));
		response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
	}
}