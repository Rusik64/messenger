package com.example.messenger.repository;

import com.example.messenger.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByIsEnabledTrueAndIdNot(Long id);
    boolean countByEmail(String email);

    Optional<User> findByTokenAndIsEnabledFalse(String token);
    List<User> findByUsernameStartingWithIgnoreCaseAndIdNot(String username, Long id);
}
