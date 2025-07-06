package io.coachify.controller.chat;

import io.coachify.dto.chat.StudentChatRoomDTO;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.StudentChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentChatRoomController {

  private final StudentChatRoomService service;

  @GetMapping("/rooms")
  public ResponseEntity<List<StudentChatRoomDTO>> rooms(
    @AuthenticationPrincipal CustomPrincipal principal) {

    return ResponseEntity.ok(
      service.getRoomsForStudent(principal.getUserId()));
  }
}
