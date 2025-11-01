package com.example.messenger.dto;

public class ReportListResp {
    Long id;
    Long repUserId;
    String repUsername;
    Long messageId;
    String messageContent;
    Long reporterId;

    public ReportListResp(Long id, Long repUserId, String repUsername, Long messageId, String messageContent, Long reporterId) {
        this.id = id;
        this.repUserId = repUserId;
        this.repUsername = repUsername;
        this.messageId = messageId;
        this.messageContent = messageContent;
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

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }
}
