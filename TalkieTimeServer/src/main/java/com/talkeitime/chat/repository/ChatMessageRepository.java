package com.talkeitime.chat.repository;

import com.talkeitime.chat.entities.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(
        Long senderId,
        Long receiverId,
        Long receiverId2,
        Long senderId2
    );
}
