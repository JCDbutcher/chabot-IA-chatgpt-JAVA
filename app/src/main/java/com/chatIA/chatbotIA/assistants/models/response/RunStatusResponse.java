package com.chatIA.chatbotIA.assistants.models.response;

import com.chatIA.chatbotIA.assistants.models.Metadata;
import com.chatIA.chatbotIA.assistants.models.Tool;
import com.chatIA.chatbotIA.assistants.models.TruncationStrategy;

import java.util.ArrayList;

public class RunStatusResponse {
    public String id;
    public String object;
    public int created_at;
    public String assistant_id;
    public String thread_id;
    public String status;
    public int started_at;
    public Object expires_at;
    public Object cancelled_at;
    public Object failed_at;
    public int completed_at;
    public Object last_error;
    public String model;
    public Object instructions;
    public Object incomplete_details;
    public ArrayList<Tool> tools;
    public Metadata metadata;
    public Usage usage;
    public double temperature;
    public double top_p;
    public int max_prompt_tokens;
    public int max_completion_tokens;
    public TruncationStrategy truncation_strategy;
    public String response_format;
    public String tool_choice;
}