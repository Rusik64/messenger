package com.example.messenger.controller;

import com.example.messenger.dto.UserResponse;
import com.example.messenger.repository.model.FriendRequest;
import com.example.messenger.repository.model.User;
import com.example.messenger.service.FriendRequestService;
import com.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class FriendRequestController {
    private final FriendRequestService friendRequestService;
    private final UserService userService;

    public FriendRequestController(FriendRequestService friendRequestService, UserService userService) {
        this.friendRequestService = friendRequestService;
        this.userService = userService;
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<List<UserResponse>> requests(@PathVariable("id") Long userId) {
        List<UserResponse> reqs = friendRequestService.getReqs(userId);
        return ResponseEntity.ok(reqs);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public ResponseEntity<List<UserResponse>> friends(@PathVariable("id") Long userId) {
        List<UserResponse> friends = friendRequestService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/send-request/{id}")
    public ResponseEntity<String> sendRequest(@PathVariable("id") Long id, Principal principal) {
        friendRequestService.save(id, principal.getName());
        return ResponseEntity.ok("Запрос отправлен");
    }

    @PostMapping("/accept-req/{id}")
    public ResponseEntity<String> accept(@PathVariable("id") Long id, Principal principal) {
        User me = userService.getByEmail(principal.getName());
        friendRequestService.acceptRequest(id, me.getId());
        return ResponseEntity.ok("Request accepted");
    }

    @PostMapping("/del-req/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id, Principal principal) {
        User me = userService.getByEmail(principal.getName());
        friendRequestService.deleteRequest(id, me.getId());
        return ResponseEntity.ok("Request deleted");
    }
}
