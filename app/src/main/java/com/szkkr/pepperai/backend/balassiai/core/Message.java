package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a message in the chat system.
 * <p>
 * This class encapsulates the role and content of a message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message
{
    @JsonProperty("role")
    private final String role;
    
    @JsonProperty("content")
    private final String content;
    
    @JsonProperty("tool_calls")
    private final List<ToolCall> toolCalls;
    
    // Using NON_NULL instead of JsonIgnore to allow custom serializers to include these fields when needed
    @JsonProperty("tool_call_id")
    private final String toolCallId;
    
    @JsonProperty("name")
    private final String name;

    /**
     * Constructor to initialize a Message with a role and content.
     *
     * @param role    the role of the message sender (e.g., user, system)
     * @param content the content of the message
     */
    public Message(@JsonProperty("role") String role,
                   @JsonProperty("content") String content) {
        this(role, content, null, null, null);
    }

    /**
     * Constructor to initialize a Message with a role, content, and tool calls.
     *
     * @param role      the role of the message sender (e.g., user, assistant, system)
     * @param content   the content of the message (can be null for tool-call messages)
     * @param toolCalls the list of tool calls (for assistant messages)
     * @param toolCallId the ID of the tool call being responded to (for tool messages)
     * @param name      the name of the function being called (for tool messages)
     */
    public Message(@JsonProperty("role") String role,
                   @JsonProperty("content") String content,
                   @JsonProperty("tool_calls") List<ToolCall> toolCalls,
                   @JsonProperty("tool_call_id") String toolCallId,
                   @JsonProperty("name") String name) {
        this.role = role;
        this.content = content;
        this.toolCalls = toolCalls != null ? new ArrayList<>(toolCalls) : null;
        this.toolCallId = toolCallId;
        this.name = name;
    }

    /**
     * Factory method to create an assistant message with tool calls.
     *
     * @param content   the content of the message (can be null)
     * @param toolCalls the list of tool calls
     * @return a new Message instance with role "assistant" and the given tool calls
     */
    public static Message createToolCallMessage(String content, List<ToolCall> toolCalls) {
        return new Message("assistant", content, toolCalls, null, null);
    }

    /**
     * Factory method to create a tool response message.
     *
     * @param toolCallId the ID of the tool call being responded to
     * @param name       the name of the function that was called
     * @param content    the content of the tool response
     * @return a new Message instance with role "tool"
     */
    public static Message createToolResponseMessage(String toolCallId, String name, String content) {
        return new Message("tool", content, null, toolCallId, name);
    }

    /**
     * Gets the role of the message sender.
     *
     * @return the role as a string
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Gets the content of the message.
     *
     * @return the content as a string
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Gets the tool calls associated with this message.
     *
     * @return the list of tool calls or null if not a tool call message
     */
    public List<ToolCall> getToolCalls() {
        return toolCalls != null ? Collections.unmodifiableList(toolCalls) : null;
    }

    /**
     * Gets the tool call ID this message is responding to.
     *
     * @return the tool call ID as a string or null if not a tool response
     */
    public String getToolCallId() {
        return toolCallId;
    }

    /**
     * Gets the name of the function being called.
     *
     * @return the function name as a string or null if not a tool message
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if this message contains tool calls.
     *
     * @return true if this message contains tool calls, false otherwise
     */
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }

    /**
     * Checks if this message is a tool response.
     *
     * @return true if this message is a tool response, false otherwise
     */
    @JsonIgnore
    public boolean isToolResponse() {
        return "tool".equals(role) && toolCallId != null;
    }
}