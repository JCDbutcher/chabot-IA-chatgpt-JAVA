package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.ThreadResponse;

public interface IThreadResponse {
    void didFetch(ThreadResponse threadResponse, String msg);
    void didError(String msg);
}
