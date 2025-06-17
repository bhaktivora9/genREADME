package io.genreadme.http.enums;


/**
 * HTTP Methods for request handling
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT");
    
    private final String method;
    
    HttpMethod(String method) {
        this.method = method;
    }
    
    public String getMethod() {
        return method;
    }
    
    public boolean isSafe() {
        return this == GET || this == HEAD || this == OPTIONS || this == TRACE;
    }
    
    public boolean isIdempotent() {
        return this == GET || this == HEAD || this == PUT || this == DELETE || 
               this == OPTIONS || this == TRACE;
    }
    
    @Override
    public String toString() {
        return method;
    }
}
