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
    public ChatMessageDTO sendMessage(SendMessageDTO sendMessageDTO) {
        Optional<User> sender = userRepository.findById(sendMessageDTO.getSenderId());
        if (sender.isEmpty()) {
            throw new RuntimeException("Sender not found");
        }
        User senderUser = sender.get();
        User receiver = userRepository
            .findById(sendMessageDTO.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

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
            sendMessageDTO.getMessage(),
            chatMessage.getCreatedAt(),
            chatMessage.getStatus()
        );
    }

    @Override
    public List<ChatMessageDTO> getMessagesHistory(Long userId, String username) {
        User sender = userRepository.findByUsername(username);

        List<ChatMessage> chatMessages =
            chatMessageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(
                sender.getId(),
                userId,
                userId,
                sender.getId()
            );
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
