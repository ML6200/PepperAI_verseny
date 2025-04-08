package com.szkkr.pepperai.backend.depricated;

public enum GroqModels
{
    DEEPSEEK_R1_DISTILL_LLAMA_70B("deepseek-r1-distill-llama-70b"),
    LLAMA3_3_70B_VERSATILE("llama-3.3-70b-versatile");

    private final String modelName;

    GroqModels(String modelName)
    {
        this.modelName = modelName;
    }

    @Override
    public String toString()
    {
        return modelName;
    }
}
