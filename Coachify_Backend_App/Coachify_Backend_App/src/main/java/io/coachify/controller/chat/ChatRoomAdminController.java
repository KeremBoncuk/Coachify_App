package io.coachify.controller.chat;

import io.coachify.dto.chat.admin.AdminChatRoomResponse;
import io.coachify.dto.chat.admin.CreateChatRoomRequest;
import io.coachify.dto.chat.admin.UpdateChatRoomRequest;
import io.coachify.service.chat.ChatRoomAdminService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatRoomAdminController {

  private final ChatRoomAdminService chatRoomService;

  /**
   * GET /admin/chat/get-chatrooms
   * Get all chat rooms with optional filters:
   * - onlyActive (true/false)
   * - mentorId or studentId (but not both)
   */
  @GetMapping("/get-chatrooms")
  public List<AdminChatRoomResponse> getChatRooms(
    @RequestParam(required = false) Boolean onlyActive,
    @RequestParam(required = false) String studentId,
    @RequestParam(required = false) String mentorId
  ) {
    return chatRoomService.getChatRooms(onlyActive, studentId, mentorId);
  }

  /**
   * GET /admin/chat/get-room-details?chatRoomId=...
   * Get metadata for a specific chat room.
   */
  @GetMapping("/get-room-details")
  public AdminChatRoomResponse getRoomDetails(@RequestParam String chatRoomId) {
    return chatRoomService.getRoomDetails(new ObjectId(chatRoomId));
  }

  /**
   * POST /admin/chat/create-chatroom
   * Create a new chat room between a student and mentor.
   */
  @PostMapping("/create-chatroom")
  @ResponseStatus(HttpStatus.CREATED)
  public AdminChatRoomResponse createChatRoom(@RequestBody CreateChatRoomRequest request) {
    return chatRoomService.createChatRoom(request);
  }

  /**
   * PUT /admin/chat/update-chatroom?chatRoomId=...
   * Activate or deactivate a chat room.
   */
  @PutMapping("/update-chatroom")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateChatRoomStatus(
    @RequestParam String chatRoomId,
    @RequestBody UpdateChatRoomRequest request
  ) {
    chatRoomService.updateChatRoomStatus(new ObjectId(chatRoomId), request.active());
  }
}
