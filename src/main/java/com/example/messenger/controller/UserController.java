package com.example.messenger.controller;

import com.example.messenger.dto.UserForm;
import com.example.messenger.dto.UserFormValidator;
import com.example.messenger.repository.model.User;
import com.example.messenger.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.validation.Valid;

import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private final UserFormValidator userFormValidator;

    public UserController(UserService userService, UserFormValidator userFormValidator) {
        this.userService = userService;
        this.userFormValidator = userFormValidator;
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        if (webDataBinder.getTarget() != null && webDataBinder.getTarget().getClass() == UserForm.class) {
            webDataBinder.setValidator(userFormValidator);
        }
    }

    @GetMapping("/sign-up")
    public ModelAndView registration(ModelAndView model) {
        model.addObject(new UserForm());
        model.setViewName("sign-up");
        return model;
    }

    @PostMapping("/sign-up")
    public ModelAndView userRegistrationSubmit(ModelAndView model, @ModelAttribute @Valid UserForm userForm, BindingResult result) {
        if(result.hasErrors()) {
            model.setViewName("sign-up");
            return model;
        }
        System.out.print("user registration");
        User newUser = userService.register(userForm);
        model.addObject("email", userForm.getEmail());
        model.setViewName("mail-confirmation");
        return model;
    }

    @GetMapping("/confirm-email")
    public ModelAndView validateEmail(ModelAndView model, @RequestParam String token) {
        Optional<User> user = userService.checkEmailToken(token);
        if (user.isEmpty()) {
            model.setViewName("mail-not-confirmed");
            return model;
        }
        model.setViewName("mail-confirmed");
        return model;
    }

    @GetMapping("/login")
    public ModelAndView loginPage(ModelAndView model) {
        model.setViewName("login");
        return model;
    }
}
