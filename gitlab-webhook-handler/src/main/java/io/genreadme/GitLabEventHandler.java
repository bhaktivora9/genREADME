package io.genreadme;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GitLabEventHandler implements HttpFunction {
    private static final Logger logger = Logger.getLogger(GitLabEventHandler.class.getName());
    private final Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        
        // Set CORS headers
        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

        // Handle preflight OPTIONS request
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatusCode(204);
            return;
        }

        try {
            // Log the request
            logger.info("Received " + request.getMethod() + " request");
            
            JsonObject responseJson = new JsonObject();
            
            switch (request.getMethod()) {
                case "GET":
                    handleGetRequest(request, responseJson);
                    break;
                case "POST":
                    handlePostRequest(request, responseJson);
                    break;
                default:
                    response.setStatusCode(405);
                    responseJson.addProperty("error", "Method not allowed");
            }

            // Set response content type and write JSON response
            response.setContentType("application/json");
            BufferedWriter writer = response.getWriter();
            writer.write(gson.toJson(responseJson));
            
        } catch (Exception e) {
            logger.severe("Error processing request: " + e.getMessage());
            response.setStatusCode(500);
            
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "Internal server error");
            errorResponse.addProperty("message", e.getMessage());
            
            response.setContentType("application/json");
            BufferedWriter writer = response.getWriter();
            writer.write(gson.toJson(errorResponse));
        }
    }

    private void handleGetRequest(HttpRequest request, JsonObject response) {
        String name = request.getFirstQueryParameter("name").orElse("World");
        response.addProperty("message", "Hello, " + name + "!");
        response.addProperty("method", "GET");
        response.addProperty("timestamp", System.currentTimeMillis());
    }

    private void handlePostRequest(HttpRequest request, JsonObject response) throws IOException {
        // Read request body
        String requestBody = request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
        
        if (requestBody.isEmpty()) {
            response.addProperty("message", "Hello from POST request!");
        } else {
            try {
                JsonObject inputJson = gson.fromJson(requestBody, JsonObject.class);
                String name = inputJson.has("name") ? inputJson.get("name").getAsString() : "World";
                response.addProperty("message", "Hello, " + name + "!");
                response.addProperty("receivedData", requestBody);
            } catch (Exception e) {
                response.addProperty("message", "Hello from POST request!");
                response.addProperty("note", "Could not parse JSON body");
                response.addProperty("receivedData", requestBody);
            }
        }
        
        response.addProperty("method", "POST");
        response.addProperty("timestamp", System.currentTimeMillis());
    }
}