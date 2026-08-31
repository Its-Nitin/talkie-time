package com.talkeitime.chat.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * DTO for {@link com.talkeitime.chat.entities.User}
 */
@Value
public class LoginDTO implements Serializable {

    @NotBlank
    String username;

    @NotBlank
    String password;
}
