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

    @Autowired
    private ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageRepository chatRepository;

    @PostMapping("send-message")
    public ResponseEntity<ChatMessageDTO> sendMessage(
        @RequestBody SendMessageDTO sendMessageDTO,
        Principal principal
    ) {
        return ResponseEntity.ok().body(chatService.sendMessage(sendMessageDTO));
    }

    @GetMapping("messages-history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesHistory(
        @PathVariable Long userId,
        Principal principal
    ) {
        return ResponseEntity
            .ok()
            .body(chatService.getMessagesHistory(userId, principal.getName()));
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessageWs(SendMessageDTO sendMessageDTO, Principal principal) {
        ChatMessageDTO save = chatService.sendMessage(sendMessageDTO);

        // Send to RECEIVER
        messagingTemplate.convertAndSendToUser(
            save.getReceiver().getUsername(),
            "/queue/messages",
            save
        );

        // Send to SENDER (so sender also sees instantly)
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages", save);
    }

    @MessageMapping("/chat.typing")
    public void typing(TypingEvent typingEvent, Principal principal) {
        if (principal == null) return;

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
        if (principal == null) return;

        String sender = principal.getName();

        ChatMessage byId = chatRepository.findById(messageAckDTO.getMessageId()).orElse(null);
        if (byId == null) {
            return;
        }

        byId.setStatus(messageAckDTO.getStatus());
        chatRepository.save(byId);
        Long receiver = messageAckDTO.getMessageId();

        messagingTemplate.convertAndSendToUser(
            byId.getSender().getUsername(),
            "/queue/ack",
            messageAckDTO
        );
    }
}
