package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.assistants.models.response.RunStatusResponse;

public interface IRunStatusResponse {
    void didFetch(RunStatusResponse runStatusResponse, String msg);
    void didError(String msg);
}
