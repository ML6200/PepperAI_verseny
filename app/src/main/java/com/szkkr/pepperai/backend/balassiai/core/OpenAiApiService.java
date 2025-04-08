package com.szkkr.pepperai.backend.balassiai.core;

/**
 * Interface for interacting with the OpenAI API.
 *
 * This interface defines methods to send messages and get the API URL.
 */
public interface OpenAiApiService
{
    /**
     * Sends a chat request to the OpenAI API.
     *
     * @param request the chat request to send
     * @return the chat response from the API
     */
    ChatResponse sendMessage(ChatRequest request);

    /**
     * Gets the URL of the OpenAI API.
     *
     * @return the API URL as a string
     */
    String getUrl();
}