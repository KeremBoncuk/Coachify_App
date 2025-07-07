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

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/student/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentChatController {

  private final ChatMessageStudentService chatService;

  

  /* 2 ─ ACTIVE ROOMS */
  @GetMapping("/rooms")
  public ResponseEntity<List<StudentChatRoomDTO>> rooms(
    @AuthenticationPrincipal CustomPrincipal p) {

    return ResponseEntity.ok(chatService.getActiveChatRoomsForStudent(p.getUserId()));
  }

  /* 3 ─ PAGINATED */
  @GetMapping("/messages")
  public ResponseEntity<StudentChatMessagesResponse> messages(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
    @RequestParam(defaultValue = "20") int limit) {

    return ResponseEntity.ok(
      chatService.getMessages(
        p.getUserId(),
        new ObjectId(chatRoomId),
        before,
        limit));
  }

  /* 4 ─ FULL DUMP */
  @GetMapping("/messages/all")
  public ResponseEntity<List<StudentChatMessageDTO>> all(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId) {

    return ResponseEntity.ok(
      chatService.getAllMessages(p.getUserId(), new ObjectId(chatRoomId)));
  }

  /* 5 ─ MARK SEEN */
  @PostMapping("/messages/seen")
  public ResponseEntity<Void> seen(
    @AuthenticationPrincipal CustomPrincipal p,
    @RequestParam String chatRoomId,
    @RequestParam
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant seenUntil) {

    chatService.markMessagesAsSeen(
      p.getUserId(), new ObjectId(chatRoomId), seenUntil);
    return ResponseEntity.ok().build();
  }
}
