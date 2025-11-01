package com.example.messenger.service;

import com.example.messenger.dto.MyProfileResp;
import com.example.messenger.dto.ProfileResponse;
import com.example.messenger.dto.UserForm;
import com.example.messenger.dto.UserResponse;
import com.example.messenger.repository.FriendRequestRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.FriendRequest;
import com.example.messenger.repository.model.Role;
import com.example.messenger.repository.model.User;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final FriendRequestService friendRequestService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, FriendRequestService friendRequestService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.friendRequestService = friendRequestService;
    }

    //sry
    public User register(@Valid UserForm userForm) {
        String passwordHash = passwordEncoder.encode(userForm.getPassword());
        User user = new User();
        user.setFirstname(userForm.getFirstname());
        user.setSecondname(userForm.getSecondname());
        user.setBirthday(userForm.getBirthday());
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPassword(passwordHash);
        user.setToken(generateToken(userForm.getEmail()));
        user.setRole(Role.USER);
        userRepository.save(user);
        System.out.print("User saved");
        mailService.sendRegistrationMail(user);
        System.out.print("Send email to" + userForm.getEmail());
        return user;
    }

    private String generateToken(String smth) {
        String token = "";
        Long secondsFromEpoch = Instant.ofEpochSecond(0L).until(Instant.now(),
                ChronoUnit.SECONDS);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String preToken = smth + secondsFromEpoch;
            byte[] array = md.digest(preToken.getBytes());
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            token = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            token = secondsFromEpoch.toString();
        }
        return token;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isUserWithEmailExist(String email) {
        return userRepository.countByEmail(email);
    }

    public Optional<User> checkEmailToken(String token) {
        Optional<User> user = userRepository.findByTokenAndIsEnabledFalse(token);
        if (user.isPresent()) {
            User updateUser = user.get();
            updateUser.setEnabled(true);
            updateUser.setToken(null);
            userRepository.save(updateUser);
        }
        return user;
    }

    public List<User> getAll(Long id) {
        return userRepository.findAllByIsEnabledTrueAndIdNot(id);
    }

    public Optional<User> getById(Long userId) {
        return userRepository.findById(userId);
    }

    public ProfileResponse getProfile(Long id, Long myId) {
        User user = userRepository.findById(id).orElseThrow();
        ProfileResponse resp = new ProfileResponse(id, user.getUsername(), user.getFirstname(), user.getSecondname(), user.getBirthday(), user.isEnabled(), friendRequestService.friendRequestCheck(myId, id));
        return resp;
    }

    public List<UserResponse> getAllByUsername(String username, Long id) {
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCaseAndIdNot(username, id);
        List<UserResponse> result = new ArrayList<>();
        users.forEach(u -> {
            result.add(new UserResponse(u.getId(), u.getFirstname(), u.getSecondname()));
        });
        return result;
    }

    public void changeUsername(String username, String newUsername) {
        User user = userRepository.findByUsername(username);
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).get();
        user.setActive(false);
        userRepository.save(user);
    }

    public MyProfileResp getMyProfile(String username) {
        User user = userRepository.findByUsername(username);
        return new MyProfileResp(user.getUsername(), user.getFirstname(), user.getSecondname(), user.getEmail(), user.getBirthday());
    }
}
