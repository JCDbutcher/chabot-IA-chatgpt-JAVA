package com.chatIA.chatbotIA.assistants.models.request;

public class MessageRequest {

    public String role;
    public String content;

    public MessageRequest() {
    }

    public MessageRequest(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
