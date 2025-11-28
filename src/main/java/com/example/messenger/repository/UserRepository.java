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
    boolean countByUsername(String username);

    Optional<User> findByTokenAndIsEnabledFalse(String token);
    Optional<User> findByTokenAndIsEnabledTrue(String token);

    List<User> findByUsernameStartingWithIgnoreCaseAndIdNotAndIsEnabledTrueAndIsActiveTrue(String username, Long id);

    User findByEmail(String email);

    Optional<User> findByToken(String token);
}
