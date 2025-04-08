package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Interface representing a chat request.
 *
 * This interface defines the structure of a chat request, including methods
 * to get the model, messages, and temperature for the request.
 */
@JsonSerialize(using = ChatRequestJsonSerializer.class)
public interface ChatRequest
{
    /**
     * Gets the model used for the chat request.
     *
     * @return the model as a string
     */
    String getModel();

    /**
     * Gets the list of messages in the chat request.
     *
     * @return the list of messages
     */
    List<Message> getMessages();

    /**
     * Gets the temperature setting for the chat request.
     *
     * @return the temperature as a float
     */
    float getTemperature();
    
    /**
     * Gets the list of tools available for the model to use.
     *
     * @return the list of tool definitions
     */
    List<ToolDefinition> getTools();
    
    /**
     * Checks if the model should use tools.
     *
     * @return true if tools should be used, false otherwise
     */
    boolean shouldUseTools();
}