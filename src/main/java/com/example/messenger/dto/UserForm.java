package com.example.messenger.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserForm {
    private Long id;
    @NotNull
    private String firstname;
    private String secondname;
    @NotNull
    @Length(min = 3, max = 20)
    @Pattern(regexp="^[a-zA-Z0-9_]+$", message="Можно использовать только символы a-z, A-Z, 0-9, символ подчеркивания.")
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 8)
    private String password;
    private LocalDate birthday;

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

    public void setSecondname(String secondname) {
        this.secondname = secondname;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
