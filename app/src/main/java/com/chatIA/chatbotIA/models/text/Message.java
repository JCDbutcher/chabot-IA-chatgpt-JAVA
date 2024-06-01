package com.chatIA.chatbotIA.models.text;

import com.google.firebase.Timestamp;

public class Message {
    private String content;
    private String sender;
    private Timestamp timestamp;

    // Constructor vacío requerido por Firestore
    public Message() {}

    // Constructor completo
    public Message(String content, String sender, Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getters y setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
