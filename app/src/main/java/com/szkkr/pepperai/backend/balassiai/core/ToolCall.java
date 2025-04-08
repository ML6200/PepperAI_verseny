package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing a tool call made by an AI model.
 * <p>
 * This class encapsulates the information about a tool call, including its ID, type, and function.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolCall {
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("type")
    private final String type;
    
    @JsonProperty("function")
    private final String function;

    /**
     * Constructor to initialize a ToolCall with its ID, type, and function call details.
     *
     * @param id       the unique identifier for the tool call
     * @param type     the type of the tool call (usually "function")
     * @param function the function call details
     */
    public ToolCall(@JsonProperty("id") String id,
                    @JsonProperty("type") String type,
                    @JsonProperty("function") FunctionCall function) {
        this.id = id;
        this.type = type;
        this.function = String.valueOf(function);
    }

    /**
     * Gets the ID of the tool call.
     *
     * @return the ID as a string
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * Gets the type of the tool call.
     *
     * @return the type as a string
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * Gets the function call details.
     *
     * @return the function call
     */
    @JsonProperty("function")
    public String getFunction() {
        return function;
    }

    /**
     * Class representing the details of a function call.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionCall {
        @JsonProperty("name")
        private final String name;
        
        @JsonProperty("arguments")
        private final String arguments;

        /**
         * Constructor to initialize a FunctionCall with a name and arguments.
         *
         * @param name      the name of the function being called
         * @param arguments the arguments for the function call (as a JSON string)
         */
        public FunctionCall(@JsonProperty("name") String name,
                           @JsonProperty("arguments") String arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        /**
         * Gets the name of the function being called.
         *
         * @return the name as a string
         */
        @JsonProperty("name")
        public String getName() {
            return name;
        }

        /**
         * Gets the arguments for the function call.
         *
         * @return the arguments as a JSON string
         */
        @JsonProperty("arguments")
        public String getArguments() {
            return arguments;
        }
    }
}
