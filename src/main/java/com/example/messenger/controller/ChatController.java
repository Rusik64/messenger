package com.example.messenger.controller;

import com.example.messenger.dto.ChatResponse;
import com.example.messenger.dto.MessageDTO;
import com.example.messenger.repository.model.ChatRoom;
import com.example.messenger.repository.model.Message;
import com.example.messenger.repository.model.User;
import com.example.messenger.service.ChatRoomService;
import com.example.messenger.service.FriendRequestService;
import com.example.messenger.service.MessageService;
import com.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Objects;


@Controller
public class ChatController {
    private final UserService userService;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FriendRequestService friendRequestService;

    public ChatController(UserService userService, MessageService messageService, ChatRoomService chatRoomService, SimpMessagingTemplate simpMessagingTemplate, FriendRequestService friendRequestService) {
        this.userService = userService;
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.friendRequestService = friendRequestService;
    }

    @GetMapping("/chat/{userId}")
    @ResponseBody
    public ResponseEntity<ChatResponse> getMessages(@PathVariable("userId") Long user2Id, Principal principal) {
        User user1 = userService.getByEmail(principal.getName());
        User user2 = userService.getById(user2Id).orElseThrow();
        ChatRoom chatRoom = chatRoomService.getOrCreate(user1, user2);
        List<Message> messages = messageService.getByChat(chatRoom.getId());    //TODO: Message -> MessageDTO
        ChatResponse chat = new ChatResponse(chatRoom.getId(), messages, friendRequestService.friendRequestCheck(user2Id, user1.getId()));
        return ResponseEntity.ok(chat);
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO message) {
        Message newMsg = messageService.save(message);
        User recipient = null;
        if (newMsg.getSender().getId().equals(newMsg.getChatRoom().getUser1().getId())) {
            recipient = newMsg.getChatRoom().getUser2();
        }
        else {
            recipient = newMsg.getChatRoom().getUser1();
        }
        simpMessagingTemplate.convertAndSendToUser(recipient.getUsername(), "/queue/messages", newMsg);
    }
}

//new ChatNotification(newMsg.getId(), newMsg.getSender().getId(), newMsg.getChatRoom().getId(), newMsg.getContent())
