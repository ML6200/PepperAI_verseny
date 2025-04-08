package com.szkkr.pepperai.backend.balassiai.core;

import android.os.Build;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Default implementation of the ToolProcessor interface.
 * <p>
 * This class provides a registry-based mechanism for handling tool calls.
 */
public class DefaultToolProcessor implements ToolProcessor {
    
    private final Map<String, Function<String, String>> functionHandlers;
    private final List<Tool> availableTools;
    private final ObjectMapper objectMapper;
    
    /**
     * Default constructor initializing empty function handlers and available tools.
     */
    public DefaultToolProcessor() {
        this.functionHandlers = new HashMap<>();
        this.availableTools = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Registers a function handler for a given tool.
     *
     * @param tool the tool to register
     * @param handler the function to handle calls to this tool
     * @return the current instance of DefaultToolProcessor
     */
    public DefaultToolProcessor registerTool(Tool tool, Function<String, String> handler) {
        String functionName = tool.getFunction().getName();
        this.functionHandlers.put(functionName, handler);
        this.availableTools.add(tool);
        return this;
    }
    
    /**
     * Unregisters a function handler for a given function name.
     *
     * @param functionName the name of the function to unregister
     * @return the current instance of DefaultToolProcessor
     */
    public DefaultToolProcessor unregisterFunction(String functionName) {
        this.functionHandlers.remove(functionName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            this.availableTools.removeIf(tool -> 
                tool.getFunction().getName().equals(functionName));
        }
        return this;
    }
    
    @Override
    public List<Message> processToolCalls(List<ToolCall> toolCalls) {
        List<Message> responses = new ArrayList<>();
        
        for (ToolCall toolCall : toolCalls) {
            String functionName = toolCall.getFunction();
            String arguments = toolCall.getFunction();
            
            if (canHandleFunction(functionName)) {
                Function<String, String> handler = functionHandlers.get(functionName);
                String responseContent = "";
                
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        responseContent = handler.apply(arguments);
                    }
                } catch (Exception e) {
                    responseContent = formatErrorResponse(e);
                }
                
                Message toolResponse = Message.createToolResponseMessage(
                        toolCall.getId(), functionName, responseContent);
                responses.add(toolResponse);
            } else {
                String errorMessage = String.format(
                        "Function '%s' is not supported by this tool processor", functionName);
                Message errorResponse = Message.createToolResponseMessage(
                        toolCall.getId(), functionName, errorMessage);
                responses.add(errorResponse);
            }
        }
        
        return responses;
    }
    
    @Override
    public boolean canHandleFunction(String functionName) {
        return functionHandlers.containsKey(functionName);
    }
    
    @Override
    public List<Tool> getAvailableTools() {
        return new ArrayList<>(availableTools);
    }
    
    /**
     * Creates a JSON error response string.
     *
     * @param exception the exception that occurred
     * @return a JSON string with error details
     */
    private String formatErrorResponse(Exception exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getClass().getSimpleName());
        errorResponse.put("message", exception.getMessage());
        
        try {
            return objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Internal error formatting error response\"}";
        }
    }
}
