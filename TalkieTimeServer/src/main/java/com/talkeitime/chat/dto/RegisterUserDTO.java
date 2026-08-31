package com.talkeitime.chat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.talkeitime.chat.entities.User}
 */
@Value
public class RegisterUserDTO implements Serializable {

    @NotBlank
    String username;

    @NotBlank
    String password;

    @NotBlank
    @Email
    String email;
}
