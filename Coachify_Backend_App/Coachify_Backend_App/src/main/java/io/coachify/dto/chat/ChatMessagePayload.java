package io.coachify.dto.chat;

import lombok.Data;
import java.util.List;

/** Payload the client sends via STOMP when sending a message. */
@Data
public class ChatMessagePayload {
  private String chatRoomId;        // HEX string
  private String text;
  private List<String> mediaUrls;   // optional
}
