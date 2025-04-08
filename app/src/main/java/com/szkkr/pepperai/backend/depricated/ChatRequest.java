package com.szkkr.pepperai.backend.depricated;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest
{
    private String model;
    private List<Message> messages;

    public ChatRequest(String model, List<Message> messages)
    {
        this.model = model;
        this.messages = messages;
    }

    public ChatRequest()
    {
        messages = new ArrayList<>();
    }

    public void addMessage(Message message)
    {
        messages.add(message);
    }

    public void addUserMessage(String message) {

        addMessage(new Message("user", message));
    }

    public void addSystemMessage(String message) {
        addMessage(new Message("system", message));
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setMemory(ChatMemory memory)
    {
        this.messages = memory.getMessages();
    }

    public String toJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
