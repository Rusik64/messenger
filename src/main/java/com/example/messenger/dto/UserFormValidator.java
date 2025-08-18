package com.example.messenger.dto;

import com.example.messenger.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserFormValidator implements Validator {

    private final UserService userService;

    public UserFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof UserForm userForm)) {
            return;
        }
        String email = userForm.getEmail();
        if (userService.isUserWithEmailExist(email)) {
            errors.rejectValue("email", "", "User with this email exist.");
        }
    }
}
