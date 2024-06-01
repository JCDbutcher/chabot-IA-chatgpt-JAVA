package com.chatIA.chatbotIA.assistants.models.request;

import com.chatIA.chatbotIA.assistants.models.Tool;

import java.util.ArrayList;

public class AssistantRequest {

    public String instructions;
    public String name;
    public ArrayList<Tool> tools;
    public String model;
    public String description;

}
