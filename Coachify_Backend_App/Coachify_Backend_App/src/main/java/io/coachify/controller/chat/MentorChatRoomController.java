package io.coachify.controller.chat;

import io.coachify.dto.chat.MentorChatRoomDTO;
import io.coachify.security.CustomPrincipal;
import io.coachify.service.chat.MentorChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentor/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MENTOR')")
public class MentorChatRoomController {

  private final MentorChatRoomService service;

  @GetMapping("/rooms")
  public ResponseEntity<List<MentorChatRoomDTO>> rooms(
    @AuthenticationPrincipal CustomPrincipal principal) {

    return ResponseEntity.ok(
      service.getRoomsForMentor(principal.getUserId()));
  }
}
