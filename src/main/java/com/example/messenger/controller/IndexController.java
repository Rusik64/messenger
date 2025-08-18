package com.example.messenger.controller;

import com.example.messenger.repository.model.User;
import com.example.messenger.service.MessageService;
import com.example.messenger.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class IndexController {

    private final UserService userService;
    private final MessageService messageService;

    public IndexController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping("/")
    public ModelAndView index(ModelAndView model, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        model.addObject("me", user);
        model.addObject("users", userService.getAll(user.getId()));
        model.setViewName("index");
        return model;
    }
}
