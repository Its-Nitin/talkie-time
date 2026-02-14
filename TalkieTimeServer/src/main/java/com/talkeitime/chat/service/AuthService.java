package com.talkeitime.chat.service;

import com.talkeitime.chat.dto.LoginDTO;
import com.talkeitime.chat.dto.RegisterUserDTO;
import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.entities.User;
import java.util.List;

import com.talkeitime.chat.utils.AuthResponse;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    UserDTO registerUser(RegisterUserDTO registerUserDTO) throws BadRequestException;
    AuthResponse loginUser(LoginDTO loginDTO) throws BadRequestException;
}
