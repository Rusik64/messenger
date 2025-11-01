package com.example.messenger.repository;

import com.example.messenger.repository.model.ChatRoom;
import com.example.messenger.repository.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatRoomIdOrderByTimestampAsc(Long chatId);
    @Query("""
    SELECT m FROM Message m
    WHERE m.chatRoom = :chatRoom
      AND m.timestamp BETWEEN :startTime AND :endTime
    ORDER BY m.timestamp ASC
""")
    List<Message> findContextMessages(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}