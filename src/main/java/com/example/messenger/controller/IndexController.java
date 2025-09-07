package com.example.messenger.controller;

import com.example.messenger.repository.model.User;
import com.example.messenger.service.FriendRequestService;
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
    private final FriendRequestService friendRequestService;

    public IndexController(UserService userService, MessageService messageService, FriendRequestService friendRequestService) {
        this.userService = userService;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
    }

    @GetMapping("/")
    public ModelAndView index(ModelAndView model, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        model.addObject("me", user);
        model.addObject("friends", friendRequestService.getFriends(user.getId()));
        model.setViewName("index");
        return model;
    }
}
