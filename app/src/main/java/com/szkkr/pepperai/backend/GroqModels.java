package com.szkkr.pepperai.backend;

public final class GroqModels
{
    public static final String DEEPSEEK_R1_DISTILL_LLAMA_70B = "deepseek-r1-distill-llama-70b";
    public static final String LLAMA3_3_70B_VERSATILE = "llama-3.3-70b-versatile";
    public static final String LLAMA3_3_70B_8192 = "llama3-70b-8192";
    public static final String LLAMA3_3_70B_SPECDEC = "llama-3.3-70b-specdec";

    // Private constructor to prevent instantiation
    private GroqModels()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
