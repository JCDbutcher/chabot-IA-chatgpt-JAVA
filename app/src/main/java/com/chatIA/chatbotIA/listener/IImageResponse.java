package com.chatIA.chatbotIA.listener;

import com.chatIA.chatbotIA.models.response.images.ImageResponse;

public interface IImageResponse {

    void didFetch(ImageResponse imageResponse, String msg);
    void didError(String msg);

}
