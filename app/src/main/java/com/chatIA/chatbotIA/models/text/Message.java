package com.chatIA.chatbotIA.models.text;

public class Message {
    private String content;
    private String sender;

    // Constructor vacío requerido por Firestore
    public Message() {}

    // Constructor completo
    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
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
    public String toString(){
        return "Message sender: " + this.getContent() + "; sender: " + this.getSender();
    }
}
