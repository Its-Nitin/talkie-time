package com.talkeitime.chat.dto;

import com.talkeitime.chat.entities.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageAckDTO {

    private Long messageId;
    private MessageStatus status;
}
