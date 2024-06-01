package com.chatIA.chatbotIA.listener;

import okhttp3.ResponseBody;

public interface IResponseBody {

    void didFetch(ResponseBody responseBody, String msg);
    void didError(String msg);

}
