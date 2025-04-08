package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing a wrapper for an error response.
 * <p>
 * This class encapsulates an error response received from the chat API.
 * It provides methods to get and set the error response.
 */
public class ErrorWrapper
{
    @JsonProperty("error")
    private ErrorResponse error;

    /**
     * Constructor to initialize the error wrapper with an error response.
     *
     * @param error the error response to set
     */
    public ErrorWrapper(ErrorResponse error)
    {
        this.error = error;
    }

    /**
     * Gets the error response.
     * <p>
     * This method returns the error response encapsulated in this wrapper.
     *
     * @return the error response
     */
    public ErrorResponse getError()
    {
        return error;
    }

    /**
     * Sets the error response.
     * <p>
     * This method sets the error response encapsulated in this wrapper.
     *
     * @param error the error response to set
     */
    public void setError(ErrorResponse error)
    {
        this.error = error;
    }
}