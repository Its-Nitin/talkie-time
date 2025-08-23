package com.talkeitime.chat.dto;

import java.io.Serializable;
import lombok.Value;

/**
 * DTO for {@link com.talkeitime.chat.entities.User}
 */
@Value
public class LoginDTO implements Serializable {

    String username;
    String password;
}
