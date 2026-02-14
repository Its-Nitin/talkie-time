package com.talkeitime.chat.presence;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PresenceController {

    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/presence.ready")
    public void presenceReady(Principal principal) {
        if (principal == null) {
            return;
        }
        Set<String> collect = simpUserRegistry
            .getUsers()
            .stream()
            .map(SimpUser::getName)
            .collect(Collectors.toSet());
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/presence", collect);
    }
}
