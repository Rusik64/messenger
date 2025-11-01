package com.example.messenger.dto;

import com.example.messenger.repository.model.Message;

import java.util.List;

//ответ на запрос чата
public class ChatResponse {
    private Long id;    //ChatRoom.id
    private List<Message> messages;
    private int status;


    public ChatResponse(Long id, List<Message> messages, int status) {
        this.id = id;
        this.messages = messages;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
