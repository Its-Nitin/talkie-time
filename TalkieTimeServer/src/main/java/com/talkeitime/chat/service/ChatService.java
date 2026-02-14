package com.talkeitime.chat.service;

import com.talkeitime.chat.dto.ChatMessageDTO;
import com.talkeitime.chat.dto.SendMessageDTO;
import java.util.List;

public interface ChatService {
    ChatMessageDTO sendMessage(SendMessageDTO sendMessageDTO);
    List<ChatMessageDTO> getMessagesHistory(Long userId, String username);
}
