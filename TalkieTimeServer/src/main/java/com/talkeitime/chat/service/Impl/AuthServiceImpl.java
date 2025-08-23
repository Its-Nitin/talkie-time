package com.talkeitime.chat.service.Impl;

import static com.talkeitime.chat.service.mapper.UserMapper.toUserDTO;

import com.talkeitime.chat.dto.LoginDTO;
import com.talkeitime.chat.dto.RegisterUserDTO;
import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.entities.User;
import com.talkeitime.chat.repository.UserRepository;
import com.talkeitime.chat.service.AuthService;
import com.talkeitime.chat.config.utils.JwtUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDTO registerUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.existsByUsername(registerUserDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setEmail(registerUserDTO.getEmail());
        user.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        User savedUser = userRepository.save(user);
        return toUserDTO(savedUser);
    }

    @Override
    public String loginUser(LoginDTO loginDTO) throws BadRequestException {
        User user = userRepository.findByUsername(loginDTO.getUsername());

        if (user == null) {
            throw new BadRequestException(
                "User not found with username: " + loginDTO.getUsername()
            );
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        return jwtUtil.generateToken(loginDTO.getUsername());
    }
}
