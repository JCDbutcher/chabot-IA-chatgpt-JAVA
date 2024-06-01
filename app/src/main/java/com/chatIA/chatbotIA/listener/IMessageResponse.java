package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.MessageResponse;

public interface IMessageResponse {
    void didFetch(MessageResponse messageResponse, String msg);
    void didError(String msg);
}
