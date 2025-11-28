package com.example.messenger.service;

import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(email);
        User user = userRepository.findByEmail(email);
        System.out.println(user.getEmail());
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("Email is not confirmed");
        }
        if (!user.isActive()) {
            throw new LockedException("User is blocked");
        }
        return new UserDetailsImpl(user);
    }
}
