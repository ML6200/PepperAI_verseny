package com.szkkr.pepperai.backend.balassiai.core;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Builder class for creating instances of OpenAiApiService.
 *
 * This class provides methods to set the URL, API key, and executor for the service.
 * It also provides methods to build the service synchronously and asynchronously.
 *
 * @author Mark Lorincz
 */
public class OpenAiApiServiceBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(OpenAiApiServiceBuilder.class);
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ChatRequest.class, new ChatRequestJsonSerializer());
        objectMapper.registerModule(module);
    }

    private String url;
    private String apiKey;
    private Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Sets the URL for the OpenAiApiService.
     *
     * @param url the URL to set
     * @return the current instance of OpenAiApiServiceBuilder
     */
    public OpenAiApiServiceBuilder withUrl(String url)
    {
        this.url = url;
        return this;
    }

    /**
     * Sets the API key for the OpenAiApiService.
     *
     * @param apiKey the API key to set
     * @return the current instance of OpenAiApiServiceBuilder
     */
    public OpenAiApiServiceBuilder withApiKey(String apiKey)
    {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Sets the executor for asynchronous operations.
     *
     * @param executor the executor to set
     * @return the current instance of OpenAiApiServiceBuilder
     */
    public OpenAiApiServiceBuilder withExecutor(Executor executor)
    {
        this.executor = executor;
        return this;
    }

    /**
     * Builds an instance of OpenAiApiService synchronously.
     *
     * @return a new instance of OpenAiApiService
     */
    public OpenAiApiService build()
    {
        validateFields();
        return new OpenAiApiServiceImpl(url, apiKey);
    }

    /**
     * Builds an instance of OpenAiApiService asynchronously.
     *
     * @return a CompletableFuture containing a new instance of OpenAiApiService
     */
    public CompletableFuture<OpenAiApiService> buildAsync()
    {
        return CompletableFuture.supplyAsync(() ->
        {
            validateFields();
            return new OpenAiApiServiceImpl(url, apiKey);
        }, executor);
    }

    /**
     * Validates the fields before building the service.
     *
     * @throws IllegalStateException if the URL is not provided
     */
    private void validateFields()
    {
        if (url == null || url.trim().isEmpty())
        {
            throw new IllegalStateException("URL must be provided.");
        }
        // Additional validation can be added here if needed
    }

    /**
     * Implementation of the OpenAiApiService interface.
     *
     * This class provides methods to send messages and get the API URL.
     */
    private static class OpenAiApiServiceImpl implements OpenAiApiService
    {
        private final String url;
        private final String apiKey;

        /**
         * Constructor to initialize the OpenAiApiServiceImpl with a URL and API key.
         *
         * @param url the URL of the API
         * @param apiKey the API key for authentication
         */
        OpenAiApiServiceImpl(String url, String apiKey)
        {
            this.url = url;
            this.apiKey = apiKey;
        }

        /**
         * Gets the URL of the OpenAiApiService.
         *
         * @return the API URL as a string
         */
        @Override
        public String getUrl()
        {
            return url;
        }

        /**
         * Sends a chat request to the OpenAI API.
         *
         * @param request the chat request to send
         * @return the chat response from the API
         */
        @Override
        public ChatResponse sendMessage(ChatRequest request)
        {
            try
            {
                // Serialize the request using Jackson
                String jsonPayload = objectMapper.writeValueAsString(request);
                
                // Debug log the payload
                logger.debug("Sending API request: {}", jsonPayload);

                HttpURLConnection connection = getHttpURLConnection(jsonPayload);
                int code = connection.getResponseCode();

                if (code != HttpURLConnection.HTTP_OK)
                {
                    String errorResponse;
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8)))
                    {
                        errorResponse = br.lines().collect(Collectors.joining());
                    }
                    String message = "HTTP error code: " + code + " Response: " + errorResponse;
                    logger.error(message);
                    return createErrorResponse(message);
                }

                String apiResponse;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
                {
                    apiResponse = br.lines().collect(Collectors.joining());
                }
                return new ChatResponse(apiResponse);

            } catch (IOException e)
            {
                logger.error("I/O error during API request", e);
                return createErrorResponse("I/O error during API request: " + e.getMessage());
            } catch (Exception e)
            {
                logger.error("Unexpected error during API request", e);
                return createErrorResponse("Unexpected error during API request: " + e.getMessage());
            }
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
            URL endpoint = new URL(url);
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
            } catch (Exception e)
            {
                throw new RuntimeException("Error serializing error response", e);
            }
        }
    }
}