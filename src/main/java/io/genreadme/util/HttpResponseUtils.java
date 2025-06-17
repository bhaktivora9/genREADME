package io.genreadme.util;

import java.util.List;
import java.util.Map;

import com.google.cloud.functions.HttpResponse;

import io.genreadme.http.enums.ContentType;
import io.genreadme.http.enums.HttpHeader;

/**
 * HTTP Response utility class for Cloud Functions Provides standardized methods
 * for setting HTTP headers and response formatting
 */
public final class HttpResponseUtils {

	private HttpResponseUtils() {
		// Utility class - prevent instantiation
	}

	/**
	 * Configure CORS headers for cross-origin requests
	 */
	public static void configureCors(HttpResponse response) {
		configureCors(response, "*", "GET, POST, OPTIONS", "Content-Type, Authorization");
	}

	/**
	 * Configure CORS headers with custom values
	 */
	public static void configureCors(HttpResponse response, String allowOrigin, String allowMethods,
			String allowHeaders) {
		Map<String, List<String>> headers = response.getHeaders();
		headers.put("Access-Control-Allow-Origin", List.of(allowOrigin));
		headers.put("Access-Control-Allow-Methods", List.of(allowMethods));
		headers.put("Access-Control-Allow-Headers", List.of(allowHeaders));
		headers.put("Access-Control-Max-Age", List.of("86400"));
	}

	/**
	 * Set content type header
	 */
	public static void setContentType(HttpResponse response, ContentType contentType) {
		response.getHeaders().put("Content-Type", List.of(contentType.getMediaType()));
	}

	/**
	 * Set custom header with single value
	 */
	public static void setHeader(HttpResponse response, String name, String value) {
		response.getHeaders().put(name, List.of(value));
	}

	/**
	 * Set security headers for production
	 */
	public static void setSecurityHeaders(HttpResponse response) {
		Map<String, List<String>> headers = response.getHeaders();
		headers.put(HttpHeader.X_CONTENT_TYPE_OPTIONS.getHeaderName(), List.of("nosniff"));
		headers.put(HttpHeader.X_FRAME_OPTIONS.getHeaderName(), List.of("DENY"));
		headers.put(HttpHeader.X_FRAME_OPTIONS.getHeaderName(), List.of("DENY"));
		headers.put(HttpHeader.X_XSS_PROTECTION.getHeaderName(), List.of("1; mode=block"));
		headers.put(HttpHeader.STRICT_TRANSPORT_SECURITY.getHeaderName(),
				List.of("max-age=31536000; includeSubDomains"));
	}

}