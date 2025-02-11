package com.szkkr.pepperai.backend.balassi_ai;

import java.util.ArrayList;
import java.util.List;

public class ChatMemory
{
    private List<Message> messages = new ArrayList<>();
    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
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
}
