package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a response from a chat API.
 *
 * This class is responsible for parsing the JSON response received from a chat API
 * and extracting the relevant content. It handles different possible structures
 * of the response, such as errors and choices, and provides methods to access
 * the content and manipulate it if necessary.
 */
public class ChatResponse
{
    private Content content;
    private List<ToolCall> toolCalls;
    private Message message;

    /**
     * Constructor that parses a JSON response from the chat API.
     *
     * This constructor takes a JSON response as a string, parses it using the
     * Jackson library, and extracts the content. It handles different possible
     * structures of the response, such as errors and choices, and sets the content
     * accordingly. If an error occurs during parsing, it sets the content to an
     * error message.
     *
     * @param jsonResponse the JSON response as a string
     */
    public ChatResponse(String jsonResponse)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseObj = mapper.readTree(jsonResponse);

            if (responseObj.has("error"))
            {
                JsonNode errorObj = responseObj.get("error");
                if (errorObj.has("message"))
                {
                    this.content = new Content("API Error: " + errorObj.get("message").asText());
                } else
                {
                    this.content = new Content("API Error: " + errorObj.toString());
                }
                this.toolCalls = new ArrayList<>();
            } else if (responseObj.has("choices"))
            {
                JsonNode choices = responseObj.get("choices");
                if (choices.isArray() && choices.size() > 0)
                {
                    JsonNode messageNode = choices.get(0).get("message");
                    if (messageNode != null)
                    {
                        // Extract content (might be null if only tool calls)
                        String contentStr = messageNode.has("content") ? 
                            messageNode.get("content").asText() : "";
                        this.content = new Content(contentStr);
                        
                        // Extract tool calls if present
                        this.toolCalls = new ArrayList<>();
                        if (messageNode.has("tool_calls") && messageNode.get("tool_calls").isArray())
                        {
                            JsonNode toolCallsNode = messageNode.get("tool_calls");
                            for (JsonNode toolCallNode : toolCallsNode)
                            {
                                String id = toolCallNode.has("id") ? 
                                    toolCallNode.get("id").asText() : "";
                                String type = toolCallNode.has("type") ? 
                                    toolCallNode.get("type").asText() : "function";
                                
                                JsonNode functionNode = toolCallNode.get("function");
                                if (functionNode != null)
                                {
                                    String name = functionNode.has("name") ? 
                                        functionNode.get("name").asText() : "";
                                    String arguments = functionNode.has("arguments") ? 
                                        functionNode.get("arguments").asText() : "{}";
                                    
                                    ToolCall.FunctionCall functionCall = 
                                        new ToolCall.FunctionCall(name, arguments);
                                    this.toolCalls.add(new ToolCall(id, type, functionCall));
                                }
                            }
                            
                            // If we have tool calls, create a Message object
                            if (!this.toolCalls.isEmpty())
                            {
                                this.message = Message.createToolCallMessage(contentStr, this.toolCalls);
                            }
                        }
                    } else
                    {
                        this.content = new Content("No content in message.");
                        this.toolCalls = new ArrayList<>();
                    }
                } else
                {
                    this.content = new Content("No response from API.");
                    this.toolCalls = new ArrayList<>();
                }
            } else
            {
                this.content = new Content("Unexpected API response structure.");
                this.toolCalls = new ArrayList<>();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            this.content = new Content("Error parsing API response: " + e.getMessage());
            this.toolCalls = new ArrayList<>();
        }
    }

    /**
     * Gets the content of the chat response.
     *
     * This method returns the content extracted from the chat API response.
     * The content is returned as a string.
     *
     * @return the content as a string
     */
    public String getContent()
    {
        return this.content.getContent();
    }

    /**
     * Gets the tool calls from the response.
     *
     * @return the list of tool calls
     */
    public List<ToolCall> getToolCalls()
    {
        return new ArrayList<>(this.toolCalls);
    }
    
    /**
     * Checks if the response contains tool calls.
     *
     * @return true if tool calls are present, false otherwise
     */
    public boolean hasToolCalls()
    {
        return this.toolCalls != null && !this.toolCalls.isEmpty();
    }
    
    /**
     * Gets the Message object from the response.
     *
     * @return the message object or null if not available
     */
    public Message getMessage()
    {
        return this.message;
    }

    /**
     * Removes <think> tags and their content from the chat response.
     *
     * This method processes the content of the chat response to remove any
     * <think> tags and their enclosed content. It uses a regular expression
     * to find and replace all occurrences of <think> tags and their content
     * with an empty string. The resulting content is returned as a new
     * Content object.
     *
     * @return a new Content object without <think> tags and their content
     */
    public Content withoutThink()
    {
        if (this.content == null)
        {
            return null;
        }

        // Regex to match <think> tags and their content
        String regex = "<think>.*?</think>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.content.getContent());

        // Replace all occurrences of <think> tags and their content with an empty string
        String replaced = matcher.replaceAll("").trim();

        return new Content(replaced);
    }
}