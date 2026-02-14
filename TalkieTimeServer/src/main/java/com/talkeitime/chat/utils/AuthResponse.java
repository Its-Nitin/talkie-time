package com.talkeitime.chat.utils;

import com.talkeitime.chat.dto.UserDTO;
import lombok.Getter;

@Getter
public class AuthResponse {

    private final String token;
    private final UserDTO user;

    public AuthResponse(String token, UserDTO userDTO) {
        this.token = token;
        this.user = userDTO;
    }
}
