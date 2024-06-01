package com.chatIA.chatbotIA.assistants.models;

import com.chatIA.chatbotIA.assistants.models.request.MessageRequest;

import java.util.List;

public class Conversation {
    private String id;
    private String userId;
    private List<MessageRequest> messages;
    private String idAssistant;
    private String title;

    public Conversation() {
    }

    public Conversation(String id, String userId, List<MessageRequest> messages, String idAssistant, String title) {
        this.id = id;
        this.userId = userId;
        this.messages = messages;
        this.idAssistant = idAssistant;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<MessageRequest> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageRequest> messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdAssistant() {
        return idAssistant;
    }

    public void setIdAssistant(String idAssistant) {
        this.idAssistant = idAssistant;
    }

    public String toString(){
        return "Conversation id: " + this.id + "; userId: " + this.userId + "; idAssistant: " + idAssistant + "; title: " + this.title + "; messages: " + this.messages;
    }
}
