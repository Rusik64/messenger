package com.example.messenger.service;

import com.example.messenger.repository.ChatRoomRepository;
import com.example.messenger.repository.model.ChatRoom;
import com.example.messenger.repository.model.User;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }


    public ChatRoom getOrCreate(User user1, User user2) {   //Найти чат между двумя пользователями
        return chatRoomRepository.findByIds(user1.getId(), user2.getId())
                .orElseGet(() -> {  //Если его еще не существует, создать
                    ChatRoom newChat = new ChatRoom();
                    newChat.setUser1(user1);
                    newChat.setUser2(user2);
                    return chatRoomRepository.save(newChat);
                });
    }
}
