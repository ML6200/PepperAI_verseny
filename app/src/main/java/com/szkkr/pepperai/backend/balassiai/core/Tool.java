package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Class representing a tool that can be called by AI models.
 * <p>
 * This class defines the structure and metadata for a tool that AI can use.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tool {
    @JsonProperty("type")
    private final String type;
    
    @JsonProperty("function")
    private final FunctionDefinition function;

    /**
     * Constructor to initialize a Tool with its type and function definition.
     *
     * @param type     the type of the tool (usually "function")
     * @param function the function definition
     */
    public Tool(@JsonProperty("type") String type,
                @JsonProperty("function") FunctionDefinition function) {
        this.type = type;
        this.function = function;
    }

    /**
     * Constructor to create a function tool with the given function definition.
     *
     * @param function the function definition
     */
    public Tool(FunctionDefinition function) {
        this("function", function);
    }

    /**
     * Gets the type of the tool.
     *
     * @return the type as a string
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the function definition of the tool.
     *
     * @return the function definition
     */
    public FunctionDefinition getFunction() {
        return function;
    }

    /**
     * Class representing a function definition for a tool.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionDefinition {
        @JsonProperty("name")
        private final String name;
        
        @JsonProperty("description")
        private final String description;
        
        @JsonProperty("parameters")
        private final Map<String, Object> parameters;

        /**
         * Constructor to initialize a FunctionDefinition with name, description, and parameters.
         *
         * @param name        the name of the function
         * @param description the description of the function
         * @param parameters  the parameters schema for the function (in JSON Schema format)
         */
        public FunctionDefinition(@JsonProperty("name") String name,
                                 @JsonProperty("description") String description,
                                 @JsonProperty("parameters") Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        /**
         * Gets the name of the function.
         *
         * @return the name as a string
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the description of the function.
         *
         * @return the description as a string
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the parameters schema of the function.
         *
         * @return the parameters schema as a Map
         */
        public Map<String, Object> getParameters() {
            return parameters;
        }
    }
}
