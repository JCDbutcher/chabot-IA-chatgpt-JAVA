package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.models.text.TextResponse;

public interface ITextResponse {

    void didFetch(TextResponse text, String msg);
    void didError(String msg);

}
