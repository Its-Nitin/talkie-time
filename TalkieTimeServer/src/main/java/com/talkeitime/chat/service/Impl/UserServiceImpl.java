package com.talkeitime.chat.service.Impl;

import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.entities.User;
import com.talkeitime.chat.repository.UserRepository;
import com.talkeitime.chat.service.UserService;
import com.talkeitime.chat.service.mapper.UserMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            java.util.Collections.emptyList()
        );
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> all = userRepository.findAll();
        return all.stream().map(UserMapper::toUserDTO).toList();
    }
}
