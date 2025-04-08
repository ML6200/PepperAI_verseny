package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Custom JSON serializer for ChatRequest objects specifically for the Groq API.
 * <p>
 * This serializer uses a very strict approach to ensure that the ChatRequest is properly
 * serialized to the format expected by the Groq API, with exact control over which fields
 * are included for each message type.
 */
public class GroqChatRequestJsonSerializer extends JsonSerializer<ChatRequest> {

    @Override
    public void serialize(ChatRequest chatRequest, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        System.out.println("DEBUG - Starting Groq serialization");
        gen.writeStartObject();
        
        // Write model
        gen.writeStringField("model", chatRequest.getModel());
        System.out.println("DEBUG - Serialized model: " + chatRequest.getModel());
        
        // Write temperature
        gen.writeNumberField("temperature", chatRequest.getTemperature());
        System.out.println("DEBUG - Serialized temperature: " + chatRequest.getTemperature());
        
        // Write messages with strict control over included fields
        gen.writeArrayFieldStart("messages");
        
        for (Message message : chatRequest.getMessages()) {
            System.out.println("DEBUG - Processing message with role: " + message.getRole());
            
            gen.writeStartObject();
            
            // Include role for all message types
            gen.writeStringField("role", message.getRole());
            
            // Check message type and include appropriate fields
            switch (message.getRole()) {
                case "system":
                case "user":
                case "assistant":
                    // Include content for all standard messages
                    if (message.getContent() != null) {
                        gen.writeStringField("content", message.getContent());
                        System.out.println("DEBUG - Message content: " + message.getContent());
                    } else {
                        gen.writeNullField("content");
                        System.out.println("DEBUG - Message content is null");
                    }
                    
                    // Only include tool_calls for assistant messages and only if present
                    if ("assistant".equals(message.getRole()) && message.hasToolCalls()) {
                        List<ToolCall> toolCalls = message.getToolCalls();
                        if (toolCalls != null && !toolCalls.isEmpty()) {
                            System.out.println("DEBUG - Assistant message has " + toolCalls.size() + " tool call(s)");
                            gen.writeArrayFieldStart("tool_calls");
                            
                            for (ToolCall toolCall : toolCalls) {
                                gen.writeStartObject();
                                
                                gen.writeStringField("id", toolCall.getId());
                                gen.writeStringField("type", toolCall.getType());
                                
                                // Write function
                                gen.writeObjectFieldStart("function");
                                String function = toolCall.getFunction();
                                gen.writeStringField("name", function);
                                gen.writeStringField("arguments", function);
                                gen.writeEndObject(); // end function
                                
                                gen.writeEndObject(); // end tool call
                            }
                            
                            gen.writeEndArray(); // end tool_calls
                        }
                    }
                    break;
                    
                case "tool":
                    // For tool messages, include content, tool_call_id, and name
                    if (message.getContent() != null) {
                        gen.writeStringField("content", message.getContent());
                        System.out.println("DEBUG - Tool message content: " + message.getContent());
                    } else {
                        gen.writeNullField("content");
                        System.out.println("DEBUG - Tool message content is null");
                    }
                    
                    // Always include tool_call_id for tool messages
                    if (message.getToolCallId() != null) {
                        gen.writeStringField("tool_call_id", message.getToolCallId());
                        System.out.println("DEBUG - Tool call ID: " + message.getToolCallId());
                    } else {
                        System.out.println("WARNING - Tool message has no tool_call_id!");
                    }
                    
                    // Include name if present
                    if (message.getName() != null) {
                        gen.writeStringField("name", message.getName());
                        System.out.println("DEBUG - Tool name: " + message.getName());
                    }
                    break;
                    
                default:
                    // Unknown role, just include content
                    if (message.getContent() != null) {
                        gen.writeStringField("content", message.getContent());
                    } else {
                        gen.writeNullField("content");
                    }
                    System.out.println("WARNING - Unknown message role: " + message.getRole());
                    break;
            }
            
            gen.writeEndObject(); // end message
        }
        
        gen.writeEndArray(); // end messages
        
        // Write tools if needed
        if (chatRequest.shouldUseTools() && !chatRequest.getTools().isEmpty()) {
            System.out.println("DEBUG - Serializing " + chatRequest.getTools().size() + " tools");
            gen.writeArrayFieldStart("tools");
            
            for (ToolDefinition toolDef : chatRequest.getTools()) {
                gen.writeStartObject();
                
                gen.writeStringField("type", toolDef.getType());
                System.out.println("DEBUG - Tool type: " + toolDef.getType());
                
                // Write function
                gen.writeObjectFieldStart("function");
                ToolDefinition.FunctionDefinition function = toolDef.getFunction();
                gen.writeStringField("name", function.getName());
                System.out.println("DEBUG - Tool function name: " + function.getName());
                gen.writeStringField("description", function.getDescription());
                
                // Write parameters
                gen.writeObjectField("parameters", function.getParameters());
                
                gen.writeEndObject(); // end function
                gen.writeEndObject(); // end tool
            }
            
            gen.writeEndArray(); // end tools
        }
        
        gen.writeEndObject();
        System.out.println("DEBUG - Finished Groq serialization");
    }
}
