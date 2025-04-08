package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing an error response from the chat API.
 *
 * This class encapsulates the error message received from the chat API.
 * It provides methods to get and set the error message.
 */
public class ErrorResponse
{
    private String message;

    /**
     * Gets the error message.
     *
     * This method returns the error message received from the chat API.
     *
     * @return the error message as a string
     */
    @JsonProperty("message")
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the error message.
     *
     * This method sets the error message received from the chat API.
     *
     * @param message the error message as a string
     */
    public void setMessage(String message)
    {
        this.message = message;
    }
}