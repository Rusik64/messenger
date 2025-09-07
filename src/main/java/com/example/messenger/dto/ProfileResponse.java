package com.example.messenger.dto;

public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private boolean isEnabled;
    private int status;

    public ProfileResponse(Long id, String username, String email, boolean isEnabled, int status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isEnabled = isEnabled;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
