package com.szkkr.pepperai.backend.balassiai.core;

import org.balassiai.exceptions.EmptyMessageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the memory of a chat session.
 * <p>
 * This class manages a list of messages that are part of a chat session.
 * It provides methods to get, set, and add messages to the chat memory.
 */
public class ChatMemory
{
    private List<Message> messages = new ArrayList<>();

    /**
     * Gets the list of messages in the chat memory.
     *
     * @return the list of messages
     */
    public List<Message> getMessages()
    {
        return messages;
    }

    /**
     * Sets the list of messages in the chat memory.
     *
     * @param messages the list of messages to set
     */
    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    /**
     * Adds a message to the chat memory.
     * <p>
     * This method adds a message to the chat memory. If the message is a standard message
     * (not a tool call or tool response) and its content is empty, it throws an EmptyMessageException.
     *
     * @param message the message to add
     * @throws RuntimeException if a standard message has empty content
     */
    public void addMessage(Message message)
    {
        // Check if the message is a standard message (not a tool call or tool response)
        // and if its content is empty
        if (!message.hasToolCalls() && !message.isToolResponse() && 
            (message.getContent() == null || message.getContent().isEmpty()))
        {
            try
            {
                throw new EmptyMessageException();
            } catch (EmptyMessageException e)
            {
                throw new RuntimeException(e);
            }
        }
        messages.add(message);
    }

    /**
     * Adds a user message to the chat memory.
     * <p>
     * This method creates a new user message and adds it to the chat memory.
     *
     * @param message the user message to add
     */
    public void addUserMessage(String message)
    {
        addMessage(new Message("user", message));
    }

    /**
     * Adds a system message to the chat memory.
     * <p>
     * This method creates a new system message and adds it to the chat memory.
     *
     * @param message the system message to add
     */
    public void addSystemMessage(String message)
    {
        addMessage(new Message("system", message));
    }
    
    /**
     * Adds an assistant message to the chat memory.
     * <p>
     * This method creates a new assistant message and adds it to the chat memory.
     *
     * @param message the assistant message to add
     */
    public void addAssistantMessage(String message)
    {
        addMessage(new Message("assistant", message));
    }
    
    /**
     * Adds a tool response message to the chat memory.
     * <p>
     * This method creates a new tool response message and adds it to the chat memory.
     *
     * @param toolCallId the ID of the tool call being responded to
     * @param functionName the name of the function that was called
     * @param content the content of the tool response
     */
    public void addToolResponse(String toolCallId, String functionName, String content)
    {
        addMessage(Message.createToolResponseMessage(toolCallId, functionName, content));
    }
}