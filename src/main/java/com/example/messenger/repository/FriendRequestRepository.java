package com.example.messenger.repository;

import com.example.messenger.repository.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) " +
            "AND fr.status = ACCEPTED")
    List<FriendRequest> findAcceptedReqs(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE fr.receiver.id = :userId " +
            "AND fr.status = WAITING")
    List<FriendRequest> findReqs(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE fr.sender.id = :userId " +
            "AND fr.status = WAITING")
    List<FriendRequest> findMyReqs(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE fr.sender.id = :user1Id AND fr.receiver.id = :user2Id " +
            "OR fr.sender.id = :user2Id AND fr.receiver.id = :user1Id")
    Optional<FriendRequest> findReq(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    Optional<FriendRequest> findBySenderId(Long id);
    Optional<FriendRequest> findByReceiverId(Long id);

    FriendRequest findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
