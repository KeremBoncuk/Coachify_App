package io.coachify.controller.ws;

import io.coachify.dto.chat.WebSocketMessage;
import io.coachify.service.chat.ChatMessageWsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageWsController {

    private final ChatMessageWsService chatMessageWsService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketMessage webSocketMessage, Principal principal) {
        chatMessageWsService.processAndBroadcastMessage(webSocketMessage, principal);
    }
}