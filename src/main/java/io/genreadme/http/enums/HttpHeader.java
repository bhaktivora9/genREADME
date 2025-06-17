package io.genreadme.http.enums;

/**
 * Standard HTTP headers for consistent usage
 */
public enum HttpHeader {
    // General Headers
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_ENCODING("Content-Encoding"),
    CACHE_CONTROL("Cache-Control"),
    EXPIRES("Expires"),
    LAST_MODIFIED("Last-Modified"),
    ETAG("ETag"),
    
    // CORS Headers
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),
    ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),
    
    // Security Headers
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),
    X_FRAME_OPTIONS("X-Frame-Options"),
    X_XSS_PROTECTION("X-XSS-Protection"),
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),
    REFERRER_POLICY("Referrer-Policy"),
    
    // Authentication Headers
    AUTHORIZATION("Authorization"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    
    // Request Headers
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_ENCODING("Accept-Encoding"),
    HOST("Host"),
    REFERER("Referer"),
    
    // Custom Application Headers
    X_API_KEY("X-API-Key"),
    X_REQUEST_ID("X-Request-ID"),
    X_FORWARDED_FOR("X-Forwarded-For"),
    X_REAL_IP("X-Real-IP");
    
    private final String headerName;
    
    HttpHeader(String headerName) {
        this.headerName = headerName;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    
    @Override
    public String toString() {
        return headerName;
    }
}
