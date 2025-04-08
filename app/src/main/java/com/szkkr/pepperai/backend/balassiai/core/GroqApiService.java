package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Service class for interacting with the Groq API.
 * <p>
 * This class implements the OpenAiApiService interface and provides methods
 * to send messages and get the API URL.
 */
public class GroqApiService implements OpenAiApiService
{
    private static final Logger logger = LoggerFactory.getLogger(GroqApiService.class);
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ChatRequest.class, new GroqChatRequestJsonSerializer());
        objectMapper.registerModule(module);
    }
    
    private final String apiKey;

    /**
     * Constructor to initialize the GroqApiService with an API key.
     *
     * @param apiKey the API key to use for authentication
     */
    public GroqApiService(String apiKey)
    {
        this.apiKey = apiKey;
    }

    /**
     * Sends a chat request to the Groq API.
     *
     * @param request the chat request to send
     * @return the chat response from the API
     */
    @Override
    public ChatResponse sendMessage(ChatRequest request)
    {
        try {
            // Serialize the request using our custom serializer
            String jsonPayload = objectMapper.writeValueAsString(request);
            
            // Debug log the payload
            logger.debug("Sending API request to Groq: {}", jsonPayload);
            System.out.println("DEBUG - Groq request JSON:");
            System.out.println(jsonPayload);
            
            HttpURLConnection connection = getHttpURLConnection(jsonPayload);
            int code = connection.getResponseCode();

            if (code != HttpURLConnection.HTTP_OK) {
                String errorResponse;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    errorResponse = br.lines().collect(Collectors.joining());
                }
                String message = "HTTP error code: " + code + " Response: " + errorResponse;
                logger.error(message);
                return createErrorResponse(message);
            }

            String apiResponse;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                apiResponse = br.lines().collect(Collectors.joining());
            }
            return new ChatResponse(apiResponse);

        } catch (IOException e) {
            logger.error("I/O error during API request", e);
            return createErrorResponse("I/O error during API request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during API request", e);
            return createErrorResponse("Unexpected error during API request: " + e.getMessage());
        }
    }

    /**
     * Gets the URL of the Groq API.
     *
     * @return the API URL as a string
     */
    @Override
    public String getUrl()
    {
        return API_URL;
    }
    
    /**
     * Creates and configures an HttpURLConnection for the API request.
     *
     * @param jsonPayload the JSON payload to send
     * @return the configured HttpURLConnection
     * @throws IOException if an I/O error occurs
     */
    private HttpURLConnection getHttpURLConnection(String jsonPayload) throws IOException
    {
        URL endpoint = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        connection.setRequestProperty("Content-Type", "application/json");

        if (apiKey != null && !apiKey.isEmpty())
        {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream())
        {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return connection;
    }

    /**
     * Creates an error response for the API request.
     *
     * @param errorMessage the error message to include in the response
     * @return a ChatResponse containing the error message
     */
    private ChatResponse createErrorResponse(String errorMessage)
    {
        try
        {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(errorMessage);
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            String errorJson = objectMapper.writeValueAsString(errorWrapper);
            return new ChatResponse(errorJson);
        } catch (JsonProcessingException e)
        {
            throw new RuntimeException("Error serializing error response", e);
        }
    }
}
