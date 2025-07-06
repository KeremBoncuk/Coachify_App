package io.coachify.controller.chat;

import io.coachify.dto.chat.ChatRoomAdminDTO;
import io.coachify.service.chat.ChatRoomAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatRoomAdminController {

  private final ChatRoomAdminService service;

  /* CREATE */
  @PostMapping("/room")
  @ResponseStatus(HttpStatus.CREATED)
  public ChatRoomAdminDTO create(@RequestParam String studentId,
                                 @RequestParam String mentorId) {
    return service.createRoom(studentId, mentorId);
  }

  /* ACTIVATE / DEACTIVATE – query-param style */
  @PutMapping("/room")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@RequestParam String roomId,
                     @RequestParam boolean active) {
    service.setActive(roomId, active);
  }

  /* LIST / FILTER */
  @GetMapping("/rooms")
  public List<ChatRoomAdminDTO> list(@RequestParam(required = false) Boolean onlyActive,
                                     @RequestParam(required = false) String studentId,
                                     @RequestParam(required = false) String mentorId) {
    return service.list(onlyActive, studentId, mentorId);
  }
}
