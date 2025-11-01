package com.example.messenger.dto;

import com.example.messenger.repository.model.Message;

import java.util.List;

public class ReportResp {
    Long id;
    Long repUserId;
    String repUsername;
    Long messageId;
    String messageContent;
    List<Message> context;
    Long reporterId;

    public ReportResp(Long id, Long repUserId, String repUsername, Long messageId, String messageContent, List<Message> context, Long reporterId) {
        this.id = id;
        this.repUserId = repUserId;
        this.repUsername = repUsername;
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.context = context;
        this.reporterId = reporterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepUserId() {
        return repUserId;
    }

    public void setRepUserId(Long repUserId) {
        this.repUserId = repUserId;
    }

    public String getRepUsername() {
        return repUsername;
    }

    public void setRepUsername(String repUsername) {
        this.repUsername = repUsername;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public List<Message> getContext() {
        return context;
    }

    public void setContext(List<Message> context) {
        this.context = context;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }
}
