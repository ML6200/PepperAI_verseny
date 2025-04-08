package com.szkkr.pepperai.backend.balassiai.core;

/**
 * Builder class for creating instances of GroqApiService.
 * <p>
 * This class extends the OpenAiApiServiceBuilder and sets the fixed URL for the Groq API.
 */
public class GroqApiServiceBuilder extends OpenAiApiServiceBuilder
{
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    /**
     * Constructor to initialize the GroqApiServiceBuilder with an API key.
     * <p>
     * This constructor sets the API key and the fixed URL for the Groq API.
     *
     * @param apiKey the API key to use for authentication
     */
    public GroqApiServiceBuilder(String apiKey)
    {
        // Use the builder to set the fixed URL and optionally the API key.
        this.withApiKey(apiKey);
        this.withUrl(API_URL);
    }
}