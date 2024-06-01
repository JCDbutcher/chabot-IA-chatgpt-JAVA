package com.chatIA.chatbotIA.assistants.models.response;


import java.util.ArrayList;

public class ListMessageResponse {
    public String object;
    public ArrayList<MessageResponse> data;
    public String first_id;
    public String last_id;
    public boolean has_more;
}
