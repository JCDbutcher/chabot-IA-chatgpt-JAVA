package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.ListMessageResponse;

public interface IListMessageResponse {
    void didFetch(ListMessageResponse listMessageResponse, String msg);
    void didError(String msg);
}
