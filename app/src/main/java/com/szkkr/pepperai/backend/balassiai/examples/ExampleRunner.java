package org.balassiai.examples;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.balassiai.core.*;
import org.balassiai.models.GroqModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ExampleRunner
{
    public static final String apiKey = "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"; // Replace with actual key
    private static final String systemMessage = "Te a Balassagyarmati Balassi Bálint Gimnázium mesterséges intelligencia alapú robotja vagy. \n" +
            "A te neved Pepi.\n" +
            "A feladatod, hogy segíts a diákoknak a tanulásban és egyéb iskolához kötődő dolgokban.\n" +
            "Ha nem kérdezik, hogy hívnak, vagy nem kérdezik a neved, akkor csak a kérdésre válaszolj! " +
            "Ha be kell mutatkoznod, akkor azt röviden tedd meg!\n" +
            "Fontos, hogy röviden és érthetően válaszolj a kérdésekre.\n" +
            "Ha trágár kifejezésekkel kérdeznek akkor figyelmeztetsd őt, hogy illedelmesen beszéljen\n" +
            "(Mogyorósi Attlila: Az iskola igazgatója. Ő biológiát tanít.)" +
            "\n";

    public static void main(String[] args)
    {
        //asyncExample();
        //threadExample();
        //threadWithGroqExample();
        toolExample();
    }

    static void toolExample() {
        System.out.println("===== Starting Tool Example =====");
        
        // 1. Create a tool processor with a calculator tool
        DefaultToolProcessor toolProcessor = new DefaultToolProcessor();
        
        // 2. Define the calculator tool
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        
        // Define the 'operation' property
        Map<String, Object> operationProperty = new HashMap<>();
        operationProperty.put("type", "string");
        operationProperty.put("description", "The operation to perform: add, subtract, multiply, or divide");
        operationProperty.put("enum", Arrays.asList("add", "subtract", "multiply", "divide"));
        
        // Define the 'a' and 'b' properties (numbers to operate on)
        Map<String, Object> aProperty = new HashMap<>();
        aProperty.put("type", "number");
        aProperty.put("description", "The first number");
        
        Map<String, Object> bProperty = new HashMap<>();
        bProperty.put("type", "number");
        bProperty.put("description", "The second number");
        
        // Combine properties
        properties.put("operation", operationProperty);
        properties.put("a", aProperty);
        properties.put("b", bProperty);
        
        // Create the parameters schema
        parameters.put("type", "object");
        parameters.put("properties", properties);
        parameters.put("required", Arrays.asList("operation", "a", "b"));
        
        // Create the calculator tool
        Tool calculatorTool = new Tool(
            new Tool.FunctionDefinition(
                "calculator",
                "Perform basic arithmetic operations on two numbers",
                parameters
            )
        );
        
        System.out.println("Calculator tool created: " + calculatorTool.getFunction().getName());
        
        // 3. Register the calculator tool handler
        toolProcessor.registerTool(calculatorTool, args -> {
            try {
                System.out.println("Tool handler invoked with args: " + args);
                // Parse the JSON arguments
                Map parsedArgs = new ObjectMapper().readValue(
                    args, Map.class);
                
                String operation = (String) parsedArgs.get("operation");
                double a = ((Number) parsedArgs.get("a")).doubleValue();
                double b = ((Number) parsedArgs.get("b")).doubleValue();
                
                System.out.println("Performing calculation: " + a + " " + operation + " " + b);
                
                double result;
                switch (operation) {
                    case "add":
                        result = a + b;
                        break;
                    case "subtract":
                        result = a - b;
                        break;
                    case "multiply":
                        result = a * b;
                        break;
                    case "divide":
                        if (b == 0) {
                            return "{\"error\": \"Cannot divide by zero\"}";
                        }
                        result = a / b;
                        break;
                    default:
                        return "{\"error\": \"Unknown operation: " + operation + "\"}";
                }
                
                String response = "{\"result\": " + result + "}";
                System.out.println("Tool result: " + response);
                return response;
            } catch (Exception e) {
                System.err.println("Tool error: " + e.getMessage());
                e.printStackTrace();
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });
        
        // 4. Create the API service
        OpenAiApiService apiService = new GroqApiService(apiKey);
        
        // 5. Set up the chat context
        ChatMemory memory = new ChatMemory();
        memory.addSystemMessage("You are a helpful assistant that can perform calculations. " +
                "Use the calculator tool when asked to perform arithmetic operations.");
        memory.addUserMessage("What is 25 + 17?");
        
        System.out.println("\n===== Initial Chat Memory =====");
        for (Message msg : memory.getMessages()) {
            System.out.println("Message role: " + msg.getRole());
            System.out.println("Message content: " + msg.getContent());
            System.out.println("Has tool calls: " + msg.hasToolCalls());
            System.out.println("Is tool response: " + msg.isToolResponse());
            System.out.println("-----");
        }
        
        // 6. Build the request with tools
        ChatRequest request = new ChatRequestBuilder()
                .setModel(GroqModels.LLAMA3_3_70B_VERSATILE)  // Use a model that supports tools
                .setMemory(memory)
                .setUseTools(true)
                .addTool(calculatorTool)
                .build();
        
        // DEBUG: Print the serialized request
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
            System.out.println("\n===== DEBUG - Serialized request: =====");
            System.out.println(requestJson);
        } catch (Exception e) {
            System.err.println("Error serializing request: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 7. Send the request and handle the response
        System.out.println("\n===== Sending request with calculator tool... =====");
        ChatResponse initialResponse = apiService.sendMessage(request);
        
        // Print full response details
        System.out.println("\n===== Initial Response =====");
        System.out.println("Response content: " + initialResponse.getContent());
        System.out.println("Has tool calls: " + initialResponse.hasToolCalls());
        
        // Check if the content indicates an error (by examining if it starts with "API Error")
        if (initialResponse.getContent() != null && initialResponse.getContent().startsWith("API Error")) {
            System.out.println("ERROR MESSAGE: " + initialResponse.getContent());
            return;
        }
        
        // Process any tool calls in the response
        if (initialResponse.hasToolCalls()) {
            System.out.println("\n===== Received tool calls from the model. Processing... =====");
            
            // Print tool call details
            for (ToolCall toolCall : initialResponse.getToolCalls()) {
                System.out.println("Tool call ID: " + toolCall.getId());
                System.out.println("Tool call type: " + toolCall.getType());
                System.out.println("Function name: " + toolCall.getFunction().getName());
                System.out.println("Function arguments: " + toolCall.getFunction().getArguments());
                System.out.println("-----");
            }
            
            List<Message> toolResponses = toolProcessor.processToolCalls(
                    initialResponse.getToolCalls());
            
            // Print tool responses
            System.out.println("\n===== Tool Responses =====");
            for (Message toolResponse : toolResponses) {
                System.out.println("Tool response role: " + toolResponse.getRole());
                System.out.println("Tool response content: " + toolResponse.getContent());
                System.out.println("Tool call ID: " + toolResponse.getToolCallId());
                System.out.println("Function name: " + toolResponse.getName());
                System.out.println("-----");
            }
            
            // Add tool responses to memory
            for (Message toolResponse : toolResponses) {
                System.out.println("Adding tool response to memory: " + toolResponse.getContent());
                memory.addMessage(toolResponse);
            }
            
            // Print updated chat memory
            System.out.println("\n===== Updated Chat Memory =====");
            for (Message msg : memory.getMessages()) {
                System.out.println("Message role: " + msg.getRole());
                System.out.println("Message content: " + msg.getContent());
                System.out.println("Has tool calls: " + msg.hasToolCalls());
                System.out.println("Is tool response: " + msg.isToolResponse());
                if (msg.isToolResponse()) {
                    System.out.println("Tool call ID: " + msg.getToolCallId());
                    System.out.println("Function name: " + msg.getName());
                }
                System.out.println("-----");
            }
            
            // Continue the conversation with the tool results
            ChatRequest followUpRequest = new ChatRequestBuilder()
                    .setModel(GroqModels.LLAMA3_3_70B_VERSATILE)  // Use a model that supports tools
                    .setMemory(memory)
                    .build();
            
            // Debug the follow-up request
            try {
                ObjectMapper mapper = new ObjectMapper();
                String followUpJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(followUpRequest);
                System.out.println("\n===== DEBUG - Follow-up request: =====");
                System.out.println(followUpJson);
            } catch (Exception e) {
                System.err.println("Error serializing follow-up request: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Get the final response after tool processing
            System.out.println("\n===== Sending follow-up request... =====");
            ChatResponse finalResponse = apiService.sendMessage(followUpRequest);
            
            System.out.println("\n===== Final Response =====");
            // Check if the content indicates an error
            if (finalResponse.getContent() != null && finalResponse.getContent().startsWith("API Error")) {
                System.out.println("ERROR MESSAGE: " + finalResponse.getContent());
            } else {
                System.out.println("Final response: " + finalResponse.getContent());
            }
        } else {
            // If no tool calls were made, just show the initial response
            System.out.println("Response: " + initialResponse.getContent());
        }
        
        System.out.println("\n===== Tool Example Complete =====");
    }
}
