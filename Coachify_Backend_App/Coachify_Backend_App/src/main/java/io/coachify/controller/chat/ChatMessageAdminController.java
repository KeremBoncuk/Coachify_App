package io.coachify.controller.chat;

import io.coachify.dto.chat.admin.AdminChatMessageResponse;
import io.coachify.dto.chat.admin.AdminSendMessageRequest;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageAdminService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatMessageAdminController {

  private final ChatMessageAdminService chatService;

  /**
   * GET /admin/chat/get-messages?chatRoomId=...
   * Get all messages in a chat room (newest first).
   */
  @GetMapping("/get-messages")
  public List<AdminChatMessageResponse> getAllMessages(
    @RequestParam String chatRoomId
  ) {
    return chatService.getAllMessages(new ObjectId(chatRoomId));
  }

  /**
   * GET /admin/chat/get-messages/limited?chatRoomId=...&before=...&limit=...
   * Get limited (paginated) messages before a timestamp.
   */
  @GetMapping("/get-messages/limited")
  public List<AdminChatMessageResponse> getLimitedMessages(
    @RequestParam String chatRoomId,
    @RequestParam(required = false) Instant before,
    @RequestParam(defaultValue = "20") int limit
  ) {
    return chatService.getLimitedMessages(new ObjectId(chatRoomId), before, limit);
  }

  /**
   * POST /admin/chat/send-message?chatRoomId=...
   * Send a message to a chat room as the currently authenticated admin.
   */
  @PostMapping("/send-message")
  @ResponseStatus(HttpStatus.CREATED)
  public AdminChatMessageResponse sendMessage(
    @RequestParam String chatRoomId,
    @RequestBody AdminSendMessageRequest request,
    @AuthenticationPrincipal CustomPrincipal principal
  ) {
    return chatService.sendMessage(new ObjectId(chatRoomId), request, principal.getUserId());
  }
}
