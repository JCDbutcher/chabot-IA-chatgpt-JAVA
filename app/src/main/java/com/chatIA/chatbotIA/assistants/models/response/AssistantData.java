package com.chatIA.chatbotIA.assistants.models.response;

import com.ren.chatbotIA.assistants.models.Metadata;
import com.ren.chatbotIA.assistants.models.ToolResources;

import java.util.ArrayList;

public class AssistantData {

    public String id;
    public String object;
    public int created_at;
    public String name;
    public Object description;
    public String model;
    public String instructions;
    public ArrayList<Object> tools;
    public ToolResources tool_resources;
    public Metadata metadata;
    public double top_p;
    public double temperature;
    public String response_format;
    public int image;

}
