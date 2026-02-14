package com.talkeitime.chat.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * DTO for {@link com.talkeitime.chat.entities.User}
 */
@Data
@NoArgsConstructor
public class UserDTO implements Serializable {

    Long id;
    String username;
    String email;
    String createdAt;
}
