package com.chatIA.chatbotIA.assistants.models.response;

import com.ren.chatbotIA.assistants.models.Metadata;

import java.util.ArrayList;

public class MessageResponse {
    public String id;
    public String object;
    public int created_at;
    public Object assistant_id;
    public String thread_id;
    public Object run_id;
    public String role;
    public ArrayList<Content> content;
    public ArrayList<Object> attachments;
    public Metadata metadata;
}
