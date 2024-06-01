package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.ListAssistantResponse;

public interface IListAssistantResponse {
    void didFetch(ListAssistantResponse listAssistantResponse, String msg);
    void didError(String msg);
}
