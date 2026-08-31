package com.talkeitime.chat.controller;

import com.talkeitime.chat.dto.ChatMessageDTO;
import com.talkeitime.chat.dto.MessageAckDTO;
import com.talkeitime.chat.dto.SendMessageDTO;
import com.talkeitime.chat.dto.TypingEvent;
import com.talkeitime.chat.entities.ChatMessage;
import com.talkeitime.chat.entities.MessageStatus;
import com.talkeitime.chat.repository.ChatMessageRepository;
import com.talkeitime.chat.service.ChatService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatResource {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatRepository;

    @PostMapping("send-message")
    public ResponseEntity<ChatMessageDTO> sendMessage(
        @RequestBody SendMessageDTO sendMessageDTO,
        Principal principal
    ) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok().body(chatService.sendMessage(sendMessageDTO, username));
    }

    @GetMapping("messages-history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesHistory(
        @PathVariable Long userId,
        Principal principal
    ) {
        String username = principal != null ? principal.getName() : "";
        return ResponseEntity
            .ok()
            .body(chatService.getMessagesHistory(userId, username));
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessageWs(SendMessageDTO sendMessageDTO, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        ChatMessageDTO save = chatService.sendMessage(sendMessageDTO, username);

        // Send to RECEIVER
        messagingTemplate.convertAndSendToUser(
            save.getReceiver().getUsername(),
            "/queue/messages",
            save
        );

        // Send to SENDER (so sender also sees instantly)
        if (principal != null) {
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages", save);
        }
    }

    @MessageMapping("/chat.typing")
    public void typing(TypingEvent typingEvent, Principal principal) {
        if (principal == null || typingEvent == null || typingEvent.getReceiverId() == null) return;

        String sender = principal.getName();
        String receiver = typingEvent.getReceiverId();

        messagingTemplate.convertAndSendToUser(
            receiver,
            "/queue/typing",
            Map.of("senderId", sender, "typing", typingEvent.isTyping())
        );
    }

    @MessageMapping("/chat.ack")
    public void MessageAcknowledge(MessageAckDTO messageAckDTO, Principal principal) {
        if (principal == null || messageAckDTO == null || messageAckDTO.getMessageId() == null || messageAckDTO.getStatus() == null) {
            return;
        }

        ChatMessage chatMessage = chatRepository.findById(messageAckDTO.getMessageId()).orElse(null);
        if (chatMessage == null) {
            return;
        }

        chatMessage.setStatus(messageAckDTO.getStatus());
        chatRepository.save(chatMessage);

        messagingTemplate.convertAndSendToUser(
            chatMessage.getSender().getUsername(),
            "/queue/ack",
            messageAckDTO
        );
    }
}
