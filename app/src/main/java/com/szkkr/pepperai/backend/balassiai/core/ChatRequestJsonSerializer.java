package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom JSON serializer for ChatRequest objects.
 * <p>
 * This serializer ensures that the ChatRequest is properly serialized to the format
 * expected by API providers, handling tools and other special fields correctly.
 */
public class ChatRequestJsonSerializer extends JsonSerializer<ChatRequest> {

    @Override
    public void serialize(ChatRequest chatRequest, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        
        // Write model
        gen.writeStringField("model", chatRequest.getModel());
        
        // Write temperature
        gen.writeNumberField("temperature", chatRequest.getTemperature());
        
        // Write messages
        gen.writeArrayFieldStart("messages");
        for (Message message : chatRequest.getMessages()) {
            gen.writeObject(message);
        }
        gen.writeEndArray();
        
        // Write tools if needed
        if (chatRequest.shouldUseTools() && !chatRequest.getTools().isEmpty()) {
            gen.writeArrayFieldStart("tools");
            for (ToolDefinition tool : chatRequest.getTools()) {
                gen.writeObject(tool);
            }
            gen.writeEndArray();
        }
        
        gen.writeEndObject();
    }
}
