package com.szkkr.pepperai.backend.balassi_ai;

public class Message
{
    private final String role;
    private final String content;

    public Message(String role, String content)
    {
        this.role = role;
        this.content = content;
    }

    public String getRole()
    {
        return role;
    }

    public String getContent()
    {
        return content;
    }

    public String toJson()
    {
        return "{\"role\":\"" + escapeJson(role) + "\",\"content\":\"" + escapeJson(content) + "\"}";
    }

    private String escapeJson(String text)
    {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
