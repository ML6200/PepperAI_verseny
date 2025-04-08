package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.balassiai.exceptions.EmptyMessageException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder class for creating ChatRequest instances.
 *
 * @author Mark Lorincz
 */
public class ChatRequestBuilder
{
    private String model;
    private List<Message> messages;
    private float temperature = 1f;
    private List<ToolDefinition> tools;
    private boolean useTools = false;

    /**
     * Default constructor initializing an empty message list.
     */
    public ChatRequestBuilder()
    {
        this.messages = new ArrayList<>();
        this.tools = new ArrayList<>();
    }

    /**
     * Constructor initializing the builder with a model and messages.
     *
     * @param model the model to be used
     * @param messages the list of messages
     */
    public ChatRequestBuilder(@JsonProperty("model") String model,
                              @JsonProperty("messages") List<Message> messages)
    {
        this.model = model;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.tools = new ArrayList<>();
    }

    /**
     * Constructor initializing the builder with a model, messages, and temperature.
     *
     * @param model the model to be used
     * @param messages the list of messages
     * @param temperature the temperature setting
     */
    public ChatRequestBuilder(@JsonProperty("model") String model,
                              @JsonProperty("messages") List<Message> messages,
                              @JsonProperty("temperature") float temperature)
    {
        this.model = model;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.temperature = temperature;
        this.tools = new ArrayList<>();
    }

    /**
     * Sets the model for the chat request.
     *
     * @param model the model to be set
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder setModel(String model)
    {
        this.model = model;
        return this;
    }

    /**
     * Adds a message to the chat request.
     *
     * @param message the message to be added
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder addMessage(Message message)
    {
        this.messages.add(message);
        return this;
    }

    /**
     * Adds a user message to the chat request.
     *
     * @param message the user message to be added
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder addUserMessage(String message)
    {
        return addMessage(new Message("user", message));
    }

    /**
     * Adds a system message to the chat request.
     *
     * @param message the system message to be added
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder addSystemMessage(String message)
    {
        return addMessage(new Message("system", message));
    }

    /**
     * Sets the memory for the chat request.
     *
     * @param memory the chat memory to be set
     * @return the current instance of ChatRequestBuilder
     * @throws RuntimeException if the memory is null or empty
     */
    public ChatRequestBuilder setMemory(ChatMemory memory)
    {
        if (memory == null || memory.getMessages().isEmpty())
        {
            try
            {
                throw new EmptyMessageException();
            } catch (EmptyMessageException e)
            {
                throw new RuntimeException(e);
            }
        }

        if (!(this.messages.isEmpty()))
        {
            this.messages = memory.getMessages();
            return this;
        }

        this.messages.addAll(memory.getMessages());

        return this;
    }

    /**
     * Adds a tool to the chat request.
     *
     * @param tool the tool to be added
     * @return the current instance of ChatRequestBuilder
     * @deprecated Use {@link #addToolDefinition(ToolDefinition)} instead
     */
    @Deprecated
    public ChatRequestBuilder addTool(Tool tool)
    {
        if (this.tools == null) {
            this.tools = new ArrayList<>();
        }
        // Convert old style Tool to new ToolDefinition if we're in a transitional state
        ToolDefinition toolDefinition = convertLegacyTool(tool);
        this.tools.add(toolDefinition);
        this.useTools = true;
        return this;
    }

    /**
     * Converts a legacy Tool to a ToolDefinition.
     * This is a helper method to support backwards compatibility.
     *
     * @param tool the legacy tool to convert
     * @return a new ToolDefinition
     */
    private ToolDefinition convertLegacyTool(Tool tool) {
        // This is a simplified conversion - may need enhancement for complex tools
        ToolDefinition.Builder builder = ToolDefinition.builder(tool.getFunction().getName())
                .withDescription(tool.getFunction().getDescription());
        
        // In a real implementation, you'd want to convert all the parameters too
        return builder.build();
    }

    /**
     * Adds a tool definition to the chat request.
     *
     * @param toolDefinition the tool definition to be added
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder addToolDefinition(ToolDefinition toolDefinition)
    {
        if (this.tools == null) {
            this.tools = new ArrayList<>();
        }
        this.tools.add(toolDefinition);
        this.useTools = true;
        return this;
    }

    /**
     * Adds multiple tools to the chat request.
     *
     * @param tools the list of tools to be added
     * @return the current instance of ChatRequestBuilder
     * @deprecated Use {@link #addToolDefinitions(List)} instead
     */
    @Deprecated
    public ChatRequestBuilder addTools(List<Tool> tools)
    {
        if (this.tools == null) {
            this.tools = new ArrayList<>();
        }
        if (tools != null && !tools.isEmpty()) {
            for (Tool tool : tools) {
                addTool(tool); // Will convert each Tool to a ToolDefinition
            }
        }
        return this;
    }

    /**
     * Adds multiple tool definitions to the chat request.
     *
     * @param toolDefinitions the list of tool definitions to be added
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder addToolDefinitions(List<ToolDefinition> toolDefinitions)
    {
        if (this.tools == null) {
            this.tools = new ArrayList<>();
        }
        if (toolDefinitions != null && !toolDefinitions.isEmpty()) {
            this.tools.addAll(toolDefinitions);
            this.useTools = true;
        }
        return this;
    }

    /**
     * Sets whether tools should be used in this request.
     *
     * @param useTools true if tools should be used, false otherwise
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder setUseTools(boolean useTools)
    {
        this.useTools = useTools;
        return this;
    }

    /**
     * Builds and returns a ChatRequest instance.
     *
     * @return a new ChatRequest instance
     */
    public ChatRequest build()
    {
        return new ChatRequestImpl(model, messages, getTemperature(), tools, useTools);
    }

    /**
     * Gets the temperature setting.
     *
     * @return the temperature setting
     */
    public float getTemperature()
    {
        return temperature;
    }

    /**
     * Sets the temperature for the chat request.
     *
     * @param temperature the temperature to be set
     * @return the current instance of ChatRequestBuilder
     */
    public ChatRequestBuilder setTemperature(float temperature)
    {
        this.temperature = temperature;
        return this;
    }

    /**
     * Implementation of the ChatRequest interface.
     */
    private static class ChatRequestImpl implements ChatRequest
    {
        private final String model;
        private final List<Message> messages;
        private final float temperature;
        private final List<ToolDefinition> tools;
        private final boolean useTools;

        public ChatRequestImpl(String model, List<Message> messages, float temperature, 
                              List<ToolDefinition> tools, boolean useTools)
        {
            this.model = model;
            this.messages = messages != null ? Collections.unmodifiableList(new ArrayList<>(messages)) : Collections.emptyList();
            this.temperature = temperature;
            this.tools = tools != null ? Collections.unmodifiableList(new ArrayList<>(tools)) : Collections.emptyList();
            this.useTools = useTools;
        }

        @Override
        public String getModel()
        {
            return model;
        }

        @Override
        public List<Message> getMessages()
        {
            return messages;
        }

        @Override
        public float getTemperature()
        {
            return temperature;
        }

        @Override
        public List<ToolDefinition> getTools()
        {
            return tools;
        }

        @Override
        public boolean shouldUseTools()
        {
            return useTools && tools != null && !tools.isEmpty();
        }
    }
}