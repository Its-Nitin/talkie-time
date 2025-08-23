package com.talkeitime.chat.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for {@link com.talkeitime.chat.entities.ChatMessage}
 */

@Data
@AllArgsConstructor
public class ChatMessageDTO implements Serializable {

    Long id;
    UserDTO sender;
    UserDTO receiver;
    String message;
    String createdAt;
}
