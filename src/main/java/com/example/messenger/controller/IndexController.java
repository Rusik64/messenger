package com.example.messenger.controller;

import com.example.messenger.repository.model.User;
import com.example.messenger.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView index(ModelAndView model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        model.addObject("me", user);
        model.setViewName("index");
        return model;
    }
}
