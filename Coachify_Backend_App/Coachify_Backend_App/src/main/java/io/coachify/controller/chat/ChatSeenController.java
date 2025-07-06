package io.coachify.controller.chat;

import io.coachify.dto.chat.SeenUpdatePayload;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSeenController {

  private final ChatMessageService svc;

  /** Client → /app/chat.seen */
  @MessageMapping("/chat.seen")
  public void handleSeen(@Payload SeenUpdatePayload p, Principal pr) {
    svc.markAsSeen((CustomPrincipal) pr, p.getChatRoomId(), p.getSeenUntil());
  }
}
