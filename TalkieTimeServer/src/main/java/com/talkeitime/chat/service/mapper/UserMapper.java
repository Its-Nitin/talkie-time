package com.talkeitime.chat.service.mapper;

import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.entities.User;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }
}
