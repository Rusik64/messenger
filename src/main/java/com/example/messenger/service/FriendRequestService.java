package com.example.messenger.service;

import com.example.messenger.dto.UserResponse;
import com.example.messenger.repository.FriendRequestRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.FriendRequest;
import com.example.messenger.repository.model.RequestStatus;
import com.example.messenger.repository.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }


    public FriendRequest save(Long id, String email) {
        FriendRequest req = new FriendRequest();
        User sender = userRepository.findByEmail(email);
        User receiver = userRepository.findById(id).orElseThrow();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setStatus(RequestStatus.WAITING);
        return friendRequestRepository.save(req);
    }

    public FriendRequest acceptRequest(Long senderId, Long receiverId) {
        FriendRequest req = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        req.setStatus(RequestStatus.ACCEPTED);
        return friendRequestRepository.save(req);
    }

    public void deleteRequest(Long id1, Long id2) {
        FriendRequest req = friendRequestRepository.findReq(id1, id2).orElseThrow();
        friendRequestRepository.delete(req);
    }

    public List<UserResponse> getFriends(Long userId) {
        List<UserResponse> friends = new ArrayList<>();
        friendRequestRepository.findAcceptedReqs(userId).forEach(fr -> {
            if (!fr.getSender().getId().equals(userId)) {
                friends.add(new UserResponse(fr.getSender().getId(), fr.getSender().getFirstname(), fr.getSender().getSecondname()));
            }
            else {
                friends.add(new UserResponse(fr.getReceiver().getId(), fr.getReceiver().getFirstname(), fr.getReceiver().getSecondname()));
            }
        });
        return friends;
    }

    public List<UserResponse> getReqs(Long userId) {
        List<UserResponse> reqs = new ArrayList<>();
        friendRequestRepository.findReqs(userId).forEach(req -> {
            if (!req.getSender().getId().equals(userId)) {
                reqs.add(new UserResponse(req.getSender().getId(), req.getSender().getFirstname(), req.getSender().getSecondname()));
            }
            else {
                reqs.add(new UserResponse(req.getReceiver().getId(), req.getReceiver().getFirstname(), req.getReceiver().getSecondname()));
            }
        });
        return reqs;
    }

    public List<UserResponse> getMyReqs(Long userId) {
        List<UserResponse> reqs = new ArrayList<>();
        friendRequestRepository.findMyReqs(userId).forEach(req -> {
            if (!req.getSender().getId().equals(userId)) {
                reqs.add(new UserResponse(req.getSender().getId(), req.getSender().getFirstname(), req.getSender().getSecondname()));
            }
            else {
                reqs.add(new UserResponse(req.getReceiver().getId(), req.getReceiver().getFirstname(), req.getReceiver().getSecondname()));
            }
        });
        return reqs;
    }

    public int friendRequestCheck(Long id1, Long id2) {
        Optional<FriendRequest> req = friendRequestRepository.findReq(id1, id2);
        if (req.isEmpty()) {
            return 0;
        }
        else if (req.get().getStatus().equals(RequestStatus.WAITING) && (req.get().getReceiver().getId() == id2)) {
            return 1;
        }
        else if (req.get().getStatus().equals(RequestStatus.WAITING) && (req.get().getReceiver().getId() == id1)) {
            return 2;
        }
        else return 3;
    }
}
