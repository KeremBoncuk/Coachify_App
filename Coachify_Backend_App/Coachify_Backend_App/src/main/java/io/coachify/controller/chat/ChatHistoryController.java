package io.coachify.controller.chat;

import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageService.ChatHistoryPage;
import io.coachify.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/chat/history")
@RequiredArgsConstructor
public class ChatHistoryController {

  private final ChatMessageService svc;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ChatHistoryPage history(
    @AuthenticationPrincipal CustomPrincipal principal,
    @RequestParam String roomId,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
    @RequestParam(defaultValue = "20") int limit) {

    return svc.getHistory(principal, new ObjectId(roomId), before, limit);
  }
}
