package io.coachify.controller.chat;

import io.coachify.dto.chat.ChatMessagePayload;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

  private final ChatMessageService chatMessageService;

  @MessageMapping("/chat.send")
  public void handleSend(
    @Payload ChatMessagePayload payload,
    Principal principal) {

    CustomPrincipal user = (CustomPrincipal) principal;
    chatMessageService.saveAndBroadcast(user, payload);
  }
}
