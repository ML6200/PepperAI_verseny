package com.szkkr.pepperai.backend.balassiai.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a tool definition that can be used by AI models.
 * This class follows the builder pattern for a fluent interface.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDefinition {
    @JsonProperty("type")
    private final String type = "function";

    @JsonProperty("function")
    private final FunctionDefinition function;

    /**
     * Private constructor used by the builder.
     */
    private ToolDefinition(FunctionDefinition function) {
        this.function = function;
    }

    /**
     * Gets the function definition.
     *
     * @return the function definition
     */
    public FunctionDefinition getFunction() {
        return function;
    }

    /**
     * Gets the type of the tool.
     *
     * @return the tool type
     */
    public String getType() {
        return type;
    }

    /**
     * Creates a new builder for a tool definition.
     *
     * @param name the name of the function
     * @return a new builder
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Represents the function definition within a tool.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionDefinition {
        @JsonProperty("name")
        private final String name;

        @JsonProperty("description")
        private final String description;

        @JsonProperty("parameters")
        private final Map<String, Object> parameters;

        private FunctionDefinition(String name, String description, Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        /**
         * Gets the name of the function.
         *
         * @return the function name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the description of the function.
         *
         * @return the function description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the parameters of the function.
         *
         * @return the function parameters
         */
        public Map<String, Object> getParameters() {
            return parameters;
        }
    }

    /**
     * Builder class for creating ToolDefinition instances.
     */
    public static class Builder {
        private final String name;
        private String description = "";
        private final Map<String, Object> properties = new HashMap<>();
        private final List<String> required = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        /**
         * Sets the description for the function.
         *
         * @param description the description
         * @return this builder
         */
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Adds a string parameter to the function.
         *
         * @param name the parameter name
         * @param description the parameter description
         * @param isRequired whether the parameter is required
         * @return this builder
         */
        public Builder addStringParameter(String name, String description, boolean isRequired) {
            Map<String, Object> param = new HashMap<>();
            param.put("type", "string");
            param.put("description", description);
            properties.put(name, param);
            if (isRequired) {
                required.add(name);
            }
            return this;
        }

        /**
         * Adds a string parameter with an enum of allowed values.
         *
         * @param name the parameter name
         * @param description the parameter description
         * @param allowedValues list of allowed values
         * @param isRequired whether the parameter is required
         * @return this builder
         */
        public Builder addEnumParameter(String name, String description, List<String> allowedValues, boolean isRequired) {
            Map<String, Object> param = new HashMap<>();
            param.put("type", "string");
            param.put("description", description);
            param.put("enum", allowedValues);
            properties.put(name, param);
            if (isRequired) {
                required.add(name);
            }
            return this;
        }

        /**
         * Adds a number parameter to the function.
         *
         * @param name the parameter name
         * @param description the parameter description
         * @param isRequired whether the parameter is required
         * @return this builder
         */
        public Builder addNumberParameter(String name, String description, boolean isRequired) {
            Map<String, Object> param = new HashMap<>();
            param.put("type", "number");
            param.put("description", description);
            properties.put(name, param);
            if (isRequired) {
                required.add(name);
            }
            return this;
        }

        /**
         * Adds a boolean parameter to the function.
         *
         * @param name the parameter name
         * @param description the parameter description
         * @param isRequired whether the parameter is required
         * @return this builder
         */
        public Builder addBooleanParameter(String name, String description, boolean isRequired) {
            Map<String, Object> param = new HashMap<>();
            param.put("type", "boolean");
            param.put("description", description);
            properties.put(name, param);
            if (isRequired) {
                required.add(name);
            }
            return this;
        }

        /**
         * Builds the ToolDefinition.
         *
         * @return a new ToolDefinition instance
         */
        public ToolDefinition build() {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("type", "object");
            parameters.put("properties", properties);
            if (!required.isEmpty()) {
                parameters.put("required", required);
            }

            FunctionDefinition functionDefinition = new FunctionDefinition(name, description, parameters);
            return new ToolDefinition(functionDefinition);
        }
    }
}
