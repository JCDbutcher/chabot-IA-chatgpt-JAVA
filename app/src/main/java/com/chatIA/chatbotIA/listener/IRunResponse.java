package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.RunResponse;

public interface IRunResponse {
    void didFetch(RunResponse runResponse, String msg);
    void didError(String msg);
}
