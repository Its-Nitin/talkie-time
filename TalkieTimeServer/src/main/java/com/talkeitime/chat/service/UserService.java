package com.talkeitime.chat.service;

import com.talkeitime.chat.dto.UserDTO;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    List<UserDTO> getAllUsers();
}
