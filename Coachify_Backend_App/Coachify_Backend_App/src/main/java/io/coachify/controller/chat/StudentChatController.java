// src/main/java/io/coachify/controller/chat/StudentChatController.java
package io.coachify.controller.chat;

import io.coachify.dto.chat.student.*;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.ChatMessageStudentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/student/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentChatController {

  private final ChatMessageStudentService chatService;

  /* 1. SEND */
  @PostMapping("/send-message")
  public ResponseEntity<Void> sendMessage(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestBody StudentSendMessageRequest req
  ) {
    chatService.sendMessageByStudent(p.getUserId(), req);
    return ResponseEntity.ok().build();
  }

  /* 2. ACTIVE ROOMS (String IDs) */
  @GetMapping("/rooms")
  public ResponseEntity<List<StudentChatRoomDTO>> rooms(
    @AuthenticationPrincipal CustomPrincipal p) {
    return ResponseEntity.ok(chatService.getActiveChatRoomsForStudent(p.getUserId()));
  }

  /* 3. PAGINATED */
  @GetMapping("/messages")
  public ResponseEntity<StudentChatMessagesResponse> messages(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
    @RequestParam(defaultValue = "20") int limit
  ) {
    Instant safeBefore = before != null ? before : Instant.now();
    return ResponseEntity.ok(
      chatService.getMessages(p.getUserId(), new ObjectId(chatRoomId), safeBefore, limit)
    );
  }

  /* 4. ALL MESSAGES */
  @GetMapping("/messages/all")
  public ResponseEntity<List<StudentChatMessageDTO>> all(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId
  ) {
    return ResponseEntity.ok(
      chatService.getAllMessages(p.getUserId(), new ObjectId(chatRoomId))
    );
  }

  /* 5. MARK SEEN */
  @PostMapping("/messages/seen")
  public ResponseEntity<Void> seen(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId,
    @RequestParam
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant seenUntil
  ) {
    chatService.markMessagesAsSeen(p.getUserId(), new ObjectId(chatRoomId), seenUntil);
    return ResponseEntity.ok().build();
  }
}
