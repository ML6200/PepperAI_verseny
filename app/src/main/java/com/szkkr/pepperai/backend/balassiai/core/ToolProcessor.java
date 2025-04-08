package com.szkkr.pepperai.backend.balassiai.core;

import java.util.List;

/**
 * Interface for processing tool calls from AI models.
 * <p>
 * This interface defines methods to process tool calls and generate tool responses.
 */
public interface ToolProcessor {
    
    /**
     * Processes a list of tool calls and generates corresponding responses.
     *
     * @param toolCalls the list of tool calls to process
     * @return a list of messages containing the tool responses
     */
    List<Message> processToolCalls(List<ToolCall> toolCalls);
    
    /**
     * Checks if this processor can handle a specific function.
     *
     * @param functionName the name of the function
     * @return true if this processor can handle the function, false otherwise
     */
    boolean canHandleFunction(String functionName);
    
    /**
     * Gets the list of tools this processor can handle.
     *
     * @return the list of tools
     */
    List<Tool> getAvailableTools();
}
