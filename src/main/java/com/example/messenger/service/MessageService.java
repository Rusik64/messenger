package com.example.messenger.service;

import com.example.messenger.dto.MessageDTO;
import com.example.messenger.repository.ChatRoomRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.Message;
import com.example.messenger.repository.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;


    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Message save(MessageDTO message) {
        Message newMsg = new Message();
        newMsg.setContent(message.getContent());
        newMsg.setSender(userRepository.findById(message.getSenderId()).orElseThrow());
        newMsg.setChatRoom(chatRoomRepository.findById(message.getChatRoomId()).orElseThrow());
        newMsg.setTimestamp(LocalDateTime.now());
        return messageRepository.save(newMsg);
    }

    public List<Message> getByChat(Long chatId) {   //Сообщения ищутся по ChatRoom
        return messageRepository.findAllByChatRoomIdOrderByTimestampAsc(chatId);
    }
}
