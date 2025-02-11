package com.szkkr.pepperai.backend.balassi_ai;

import java.util.List;
import com.google.gson.Gson;

public class ChatRequest {
    private final String model;
    private final List<Message> messages;

    public ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
