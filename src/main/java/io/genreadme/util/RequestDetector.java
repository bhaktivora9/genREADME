package io.genreadme.util;

import com.google.cloud.functions.HttpRequest;

public class RequestDetector {

	public boolean isGitLabWebhook(HttpRequest request, String body) {

		// 1. Check GitLab-specific headers
		String gitlabEvent = getHeader(request, "X-Gitlab-Event");
		if (gitlabEvent != null) {
			return true;
		}

		// 2. Check request body structure
		if (body.contains("\"object_kind\"") && body.contains("\"project_id\"")) {
			return true;
		}

		// 3. Check user agent
		String userAgent = getHeader(request, "User-Agent");
		if (userAgent != null && userAgent.contains("GitLab")) {
			return true;
		}

		return false;
	}

	private String getHeader(HttpRequest request, String headerName) {
		return request.getHeaders().get(headerName) != null ? request.getHeaders().get(headerName).get(0) : null;
	}
}