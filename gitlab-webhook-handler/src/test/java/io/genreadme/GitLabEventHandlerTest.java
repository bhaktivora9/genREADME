package io.genreadme;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

public class GitLabEventHandlerTest {

    @Mock
    private HttpRequest request;

    @Mock
    private HttpResponse response;

    private GitLabEventHandler function;
    private StringWriter responseOut;
    private BufferedWriter writer;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        function = new GitLabEventHandler();
        
        responseOut = new StringWriter();
        writer = new BufferedWriter(responseOut);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testGetRequestWithoutName() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getFirstQueryParameter("name")).thenReturn(Optional.empty());

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        String responseBody = responseOut.toString();
        assertTrue("Response should contain 'Hello, World!'", 
                   responseBody.contains("Hello, World!"));
        assertTrue("Response should contain method GET", 
                   responseBody.contains("\"method\":\"GET\""));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testGetRequestWithName() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getFirstQueryParameter("name")).thenReturn(Optional.of("Alice"));

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        String responseBody = responseOut.toString();
        assertTrue("Response should contain 'Hello, Alice!'", 
                   responseBody.contains("Hello, Alice!"));
        assertTrue("Response should contain method GET", 
                   responseBody.contains("\"method\":\"GET\""));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testPostRequestWithEmptyBody() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        String responseBody = responseOut.toString();
        assertTrue("Response should contain POST message", 
                   responseBody.contains("Hello from POST request!"));
        assertTrue("Response should contain method POST", 
                   responseBody.contains("\"method\":\"POST\""));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testPostRequestWithJsonBody() throws IOException {
        // Arrange
        String jsonBody = "{\"name\":\"Bob\"}";
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        String responseBody = responseOut.toString();
        assertTrue("Response should contain 'Hello, Bob!'", 
                   responseBody.contains("Hello, Bob!"));
        assertTrue("Response should contain received data", 
                   responseBody.contains("receivedData"));
        assertTrue("Response should contain method POST", 
                   responseBody.contains("\"method\":\"POST\""));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testPostRequestWithInvalidJson() throws IOException {
        // Arrange
        String invalidJson = "invalid json";
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        String responseBody = responseOut.toString();
        assertTrue("Response should contain fallback message", 
                   responseBody.contains("Hello from POST request!"));
        assertTrue("Response should contain note about JSON parsing", 
                   responseBody.contains("Could not parse JSON body"));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testOptionsRequest() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("OPTIONS");

        // Act
        function.service(request, response);

        // Assert
        verify(response).setStatusCode(204);
        verify(response).appendHeader("Access-Control-Allow-Origin", "*");
        verify(response).appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        verify(response).appendHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Test
    public void testUnsupportedMethod() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("DELETE");

        // Act
        function.service(request, response);
        writer.flush();

        // Assert
        verify(response).setStatusCode(405);
        String responseBody = responseOut.toString();
        assertTrue("Response should contain error message", 
                   responseBody.contains("Method not allowed"));
        verify(response).setContentType("application/json");
    }

    @Test
    public void testCorsHeaders() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getFirstQueryParameter("name")).thenReturn(Optional.empty());

        // Act
        function.service(request, response);

        // Assert
        verify(response).appendHeader("Access-Control-Allow-Origin", "*");
        verify(response).appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        verify(response).appendHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}