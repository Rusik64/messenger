package com.example.messenger.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class UserForm {
    private Long id;
    @NotNull
    @Length(min = 3, max = 32)
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 8)
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Length(min = 3, max = 32) String getUsername() {
        return username;
    }

    public void setUsername(@Length(min = 3, max = 32) String username) {
        this.username = username;
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }

    public @Length(min = 8) String getPassword() {
        return password;
    }

    public void setPassword(@Length(min = 8) String password) {
        this.password = password;
    }
}
