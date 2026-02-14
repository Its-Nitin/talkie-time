package com.talkeitime.chat.presence;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@AllArgsConstructor
@Component
public class PresenceListener {

    private final SimpUserRegistry userRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        broadcast();
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        broadcast();
    }

    private void broadcast() {
        Set<String> onlineUsers = userRegistry
            .getUsers()
            .stream()
            .map(SimpUser::getName)
            .collect(Collectors.toSet());

        messagingTemplate.convertAndSend("/topic/presence", onlineUsers);
    }
}
