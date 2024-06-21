package com.chatIA.chatbotIA.assistants.models.response;

import com.ren.chatbotIA.assistants.models.Metadata;
import com.ren.chatbotIA.assistants.models.ToolResources;

public class ThreadResponse {
    public String id;
    public String object;
    public int created_at;
    public Metadata metadata;
    public ToolResources tool_resources;
}
