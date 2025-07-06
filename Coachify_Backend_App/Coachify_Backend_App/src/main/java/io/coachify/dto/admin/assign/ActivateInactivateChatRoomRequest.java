package io.coachify.dto.admin.assign;

import lombok.Data;

/**
 * Simple request body carrying the chat-room ID that an admin
 * wants to activate or inactivate.
 */
@Data
public class ActivateInactivateChatRoomRequest {
  private String chatRoomId;   // ObjectId hex string
}
