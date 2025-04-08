package org.balassiai.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.balassiai.core.ChatMemory;
import org.balassiai.core.ChatRequest;
import org.balassiai.core.ChatRequestBuilder;
import org.balassiai.core.ChatResponse;
import org.balassiai.core.Message;
import org.balassiai.core.ToolCall;
import org.balassiai.models.GroqModels;
import org.balassiai.core.GroqApiService;
import org.balassiai.tools.CalculatorTool;
import org.balassiai.tools.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Example demonstrating the new OOP-based tool design
 */
public class ToolCallExample {
    private static final Logger logger = LoggerFactory.getLogger(ToolCallExample.class);
    private static final String API_KEY = "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"; // Replace with actual key

    public static void main(String[] args) {
        runExample();
    }
    
    public static void runExample() {
        logger.info("Starting Tool Call Example");
        
        // 1. Create a tool manager and register tools
        ToolManager toolManager = new ToolManager();
        
        // 2. Create and register the calculator tool
        ToolHandler calculatorTool = new CalculatorTool();
        toolManager.registerTool(calculatorTool);
        
        logger.info("Registered calculator tool: {}", 
            calculatorTool.getToolDefinition().getFunction().getName());
        
        // 3. Create the API service
        GroqApiService apiService = new GroqApiService(API_KEY);
        
        // 4. Set up the chat context
        ChatMemory memory = new ChatMemory();
        memory.addSystemMessage("You are a helpful assistant that can perform calculations. " +
                "Use the calculator tool when asked to perform arithmetic operations.");
        memory.addUserMessage("What is 2000+2333?");
        
        logger.info("Initial Chat Memory:");
        for (Message msg : memory.getMessages()) {
            logger.info("  Role: {}, Content: {}", msg.getRole(), msg.getContent());
        }
        
        // 5. Build the request with tools
        ChatRequest request = new ChatRequestBuilder()
                .setModel(GroqModels.LLAMA3_3_70B_VERSATILE)
                .setMemory(memory)
                .setUseTools(true)
                .addToolDefinition(calculatorTool.getToolDefinition())
                .build();
        
        // Log the serialized request for debugging
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
            logger.debug("Serialized request: {}", requestJson);
        } catch (Exception e) {
            logger.error("Error serializing request: {}", e.getMessage(), e);
        }
        
        // 6. Send the initial request
        logger.info("Sending request with calculator tool");
        ChatResponse initialResponse = apiService.sendMessage(request);
        
        logger.info("Initial response content: {}", initialResponse.getContent());
        logger.info("Has tool calls: {}", initialResponse.hasToolCalls());
        
        // Check if the API reported an error
        if (initialResponse.getContent() != null && initialResponse.getContent().startsWith("API Error")) {
            logger.error("API Error: {}", initialResponse.getContent());
            return;
        }
        
        // 7. Process any tool calls in the response
        if (initialResponse.hasToolCalls()) {
            logger.info("Processing tool calls from model");
            
            List<ToolCall> toolCalls = initialResponse.getToolCalls();
            for (ToolCall toolCall : toolCalls) {
                logger.info("Tool call - ID: {}, Function: {}, Args: {}", 
                    toolCall.getId(), 
                    toolCall.getFunction().getName(),
                    toolCall.getFunction().getArguments());
            }
            
            // Process tool calls using the ToolManager
            List<Message> toolResponses = toolManager.processToolCalls(toolCalls);
            
            // Log tool responses
            logger.info("Tool responses:");
            for (Message toolResponse : toolResponses) {
                logger.info("  Tool: {}, ID: {}, Content: {}", 
                    toolResponse.getName(),
                    toolResponse.getToolCallId(),
                    toolResponse.getContent());
                
                // Add tool response to memory
                memory.addMessage(toolResponse);
            }
            
            // 8. Continue conversation with tool results
            ChatRequest followUpRequest = new ChatRequestBuilder()
                    .setModel(GroqModels.LLAMA3_3_70B_VERSATILE)
                    .setMemory(memory)
                    .build();
            
            // Log the follow-up request
            try {
                ObjectMapper mapper = new ObjectMapper();
                String followUpJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(followUpRequest);
                logger.debug("Follow-up request: {}", followUpJson);
            } catch (Exception e) {
                logger.error("Error serializing follow-up request: {}", e.getMessage(), e);
            }
            
            // 9. Get final response after tool processing
            logger.info("Sending follow-up request");
            ChatResponse finalResponse = apiService.sendMessage(followUpRequest);
            
            // Check for API errors in the final response
            if (finalResponse.getContent() != null && finalResponse.getContent().startsWith("API Error")) {
                logger.error("API Error in final response: {}", finalResponse.getContent());
            } else {
                logger.info("Final response: {}", finalResponse.getContent());
            }
        } else {
            // If no tool calls were made
            logger.info("No tool calls were made. Response: {}", initialResponse.getContent());
        }
        
        logger.info("Tool Call Example Complete");
    }
}
