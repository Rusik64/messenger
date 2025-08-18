package com.example.messenger.repository;

import com.example.messenger.repository.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
    SELECT c 
    FROM ChatRoom c
    WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id)
       OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)
    """)
    Optional<ChatRoom> findByIds(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
