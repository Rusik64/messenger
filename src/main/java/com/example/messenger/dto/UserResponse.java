package com.example.messenger.dto;

public class UserResponse {
    private Long id;
    private String firstname;
    private String secondname;

    public UserResponse(Long id, String firstname, String secondname) {
        this.id = id;
        this.firstname = firstname;
        this.secondname = secondname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSeocndname(String secondname) {
        this.secondname = secondname;
    }
}
