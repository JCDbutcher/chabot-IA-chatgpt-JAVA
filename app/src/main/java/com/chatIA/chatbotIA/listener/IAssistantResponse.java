package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.AssistantResponse;

public interface IAssistantResponse {
    void didFetch(AssistantResponse assistantResponse, String msg);
    void didError(String msg);
}
