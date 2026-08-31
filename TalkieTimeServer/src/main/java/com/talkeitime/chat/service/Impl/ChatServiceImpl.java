package com.talkeitime.chat.service.Impl;

import static com.talkeitime.chat.service.mapper.UserMapper.toUserDTO;

import com.talkeitime.chat.dto.ChatMessageDTO;
import com.talkeitime.chat.dto.SendMessageDTO;
import com.talkeitime.chat.entities.ChatMessage;
import com.talkeitime.chat.entities.MessageStatus;
import com.talkeitime.chat.entities.User;
import com.talkeitime.chat.repository.ChatMessageRepository;
import com.talkeitime.chat.repository.UserRepository;
import com.talkeitime.chat.service.ChatService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessageDTO sendMessage(SendMessageDTO sendMessageDTO, String senderUsername) {
        User senderUser;
        if (senderUsername != null && !senderUsername.trim().isEmpty()) {
            senderUser = userRepository.findByUsername(senderUsername);
            if (senderUser == null) {
                throw new RuntimeException("Authenticated sender not found: " + senderUsername);
            }
        } else if (sendMessageDTO.getSenderId() != null) {
            senderUser = userRepository.findById(sendMessageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + sendMessageDTO.getSenderId()));
        } else {
            throw new RuntimeException("Sender identity required");
        }

        User receiver = userRepository
            .findById(sendMessageDTO.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + sendMessageDTO.getReceiverId()));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(sendMessageDTO.getMessage());
        chatMessage.setSender(senderUser);
        chatMessage.setReceiver(receiver);
        chatMessage.setStatus(MessageStatus.SENT);
        chatMessage.setCreatedAt(String.valueOf(System.currentTimeMillis()));

        chatMessage = chatMessageRepository.save(chatMessage);
        return new ChatMessageDTO(
            chatMessage.getId(),
            toUserDTO(senderUser),
            toUserDTO(receiver),
            chatMessage.getMessage(),
            chatMessage.getCreatedAt(),
            chatMessage.getStatus()
        );
    }

    @Override
    public List<ChatMessageDTO> getMessagesHistory(Long userId, String username) {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            return List.of();
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findChatHistory(currentUser.getId(), userId);
        return chatMessages
            .stream()
            .map(chatMessage ->
                new ChatMessageDTO(
                    chatMessage.getId(),
                    toUserDTO(chatMessage.getSender()),
                    toUserDTO(chatMessage.getReceiver()),
                    chatMessage.getMessage(),
                    chatMessage.getCreatedAt(),
                    chatMessage.getStatus()
                )
            )
            .toList();
    }
}
