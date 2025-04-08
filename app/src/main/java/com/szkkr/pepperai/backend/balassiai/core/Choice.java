package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing a choice in the chat response.
 * <p>
 * This class encapsulates a message that is part of a choice in the chat response.
 * It provides methods to get and set the message.
 */
public class Choice
{
    private Message message;

    /**
     * Gets the message associated with this choice.
     * <p>
     * This method returns the message object that is part of this choice.
     *
     * @return the message object
     */
    @JsonProperty("message")
    public Message getMessage()
    {
        return message;
    }

    /**
     * Sets the message for this choice.
     * <p>
     * This method sets the message object for this choice.
     *
     * @param message the message object to set
     */
    public void setMessage(Message message)
    {
        this.message = message;
    }
}