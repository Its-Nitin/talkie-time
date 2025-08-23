package com.talkeitime.chat.dto;

import java.io.Serializable;
import lombok.Value;

/**
 * DTO for {@link com.talkeitime.chat.entities.ChatMessage}
 */
@Value
public class SendMessageDTO implements Serializable {

    Long receiverId;
    String message;
}
