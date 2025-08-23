package com.talkeitime.chat.controller;

import com.talkeitime.chat.dto.ChatMessageDTO;
import com.talkeitime.chat.dto.SendMessageDTO;
import com.talkeitime.chat.service.ChatService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatResource {

    @Autowired
    private ChatService chatService;

    @PostMapping("send-message")
    public ResponseEntity<ChatMessageDTO> sendMessage(
        @RequestBody SendMessageDTO sendMessageDTO,
        Principal principal
    ) {
        return ResponseEntity.ok().body(
            chatService.sendMessage(sendMessageDTO, principal.getName())
        );
    }

    @GetMapping("messages-history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesHistory(
        @PathVariable Long userId,
        Principal principal
    ) {
        return ResponseEntity.ok().body(
            chatService.getMessagesHistory(userId, principal.getName())
        );
    }
}
