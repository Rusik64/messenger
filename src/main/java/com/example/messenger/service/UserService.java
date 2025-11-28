package com.example.messenger.service;

import com.example.messenger.dto.MyProfileResp;
import com.example.messenger.dto.ProfileResponse;
import com.example.messenger.dto.UserForm;
import com.example.messenger.dto.UserResponse;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public User register(UserForm form) {
        if (userRepository.countByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует.");
        }

        if (userRepository.countByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует.");
        }

        User user = new User();
        user.setFirstname(form.getFirstname());
        user.setSecondname(form.getSecondname());
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        String token = generateToken(form.getEmail());
        user.setToken(token);

        userRepository.save(user);

        try {
            mailService.sendRegistrationMail(user);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить письмо: " + e.getMessage(), e);
        }

        return user;
    }

    public void createPasswordResetToken (String email) {
        User user = userRepository.findByEmail(email);
        String token = generateToken(email);
        user.setToken(token);
        userRepository.save(user);
        mailService.sendPwResetMail(user);
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

    public boolean isUserWithUsernameExist(String username) {
        return userRepository.countByUsername(username);
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

    public Optional<User> checkEmailTokenPR(String token) {
        System.out.println(token);
        Optional<User> user = userRepository.findByTokenAndIsEnabledTrue(token);
        System.out.println(user.get().getUsername());
        return user;
    }

    public void changePassword(String newPassword, String token) {
        User user = userRepository.findByToken(token).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setToken(null);
        userRepository.save(user);
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
        List<User> users = userRepository.findByUsernameStartingWithIgnoreCaseAndIdNotAndIsEnabledTrueAndIsActiveTrue(username, id);
        List<UserResponse> result = new ArrayList<>();
        users.forEach(u -> {
            result.add(new UserResponse(u.getId(), u.getFirstname(), u.getSecondname()));
        });
        return result;
    }

    public void changeUsername(String email, String newUsername) {
        if (!newUsername.matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Имя пользователя может содержать только буквы, цифры, точки, дефисы и подчёркивания (3–20 символов).");
        }

        if (userRepository.findByUsername(newUsername) != null) {
            throw new IllegalArgumentException("Это имя пользователя уже занято.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public MyProfileResp getMyProfile(String email) {
        User user = userRepository.findByEmail(email);
        return new MyProfileResp(user.getUsername(), user.getFirstname(), user.getSecondname(), user.getEmail(), user.getBirthday());
    }

    public void changeFirstname(String email, String newFirstname) {
        User user = userRepository.findByEmail(email);
        user.setFirstname(newFirstname);
        userRepository.save(user);
    }

    public void changeSecondname(String email, String newSecondname) {
        User user = userRepository.findByEmail(email);
        user.setSecondname(newSecondname);
        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
