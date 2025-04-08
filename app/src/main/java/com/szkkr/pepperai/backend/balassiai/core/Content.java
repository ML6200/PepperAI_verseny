package com.szkkr.pepperai.backend.balassiai.core;

/**
 * Class representing the content of a chat response.
 * <p>
 * This class encapsulates the content of a chat response and provides methods
 * to get and set the content.
 */
public class Content
{
    private String content;

    /**
     * Constructor to initialize the content.
     *
     * @param content the content as a string
     */
    public Content(String content)
    {
        this.content = content;
    }

    /**
     * Sets the content.
     *
     * @param content the content as a string
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * Gets the content.
     *
     * @return the content as a string
     */
    public String getContent()
    {
        return content;
    }
}