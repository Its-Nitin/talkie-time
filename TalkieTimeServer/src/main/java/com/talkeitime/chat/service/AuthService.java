package com.talkeitime.chat.service;

import com.talkeitime.chat.dto.LoginDTO;
import com.talkeitime.chat.dto.RegisterUserDTO;
import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.entities.User;
import java.util.List;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    UserDTO registerUser(RegisterUserDTO registerUserDTO) throws BadRequestException;
    String loginUser(LoginDTO loginDTO) throws BadRequestException;
}
