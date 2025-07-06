// SeenUpdatePayload.java  (src/main/java/io/coachify/dto/chat)
package io.coachify.dto.chat;

import lombok.Data;
import java.time.Instant;

/** Client → marks messages up to `seenUntil` as seen. */
@Data
public class SeenUpdatePayload {
  private String chatRoomId;           // HEX
  private Instant seenUntil;           // ISO-8601 — inclusive
}
