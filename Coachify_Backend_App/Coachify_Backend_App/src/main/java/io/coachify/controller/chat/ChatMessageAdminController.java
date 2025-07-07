package io.coachify.controller.chat;

import io.coachify.dto.chat.admin.*;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageAdminService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatMessageAdminController {

  private final ChatMessageAdminService chatService;

  /* legacy full dump (unchanged) */
  @GetMapping("/get-messages")
  public List<AdminChatMessageResponse> getAllMessages(
    @RequestParam String chatRoomId) {
    return chatService.getAllMessages(new ObjectId(chatRoomId));
  }

  /* NEW: paginated endpoint */
  @GetMapping("/get-messages/page")
  public AdminChatMessagePage getMessagesPage(
    @RequestParam String chatRoomId,
    @RequestParam(required = false) Instant before,
    @RequestParam(required = false) Integer limit) {

    return chatService.getPaginatedMessages(new ObjectId(chatRoomId), before, limit);
  }

  
}
