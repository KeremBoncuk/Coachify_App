// SeenAck.java  (broadcast back so other users update UI)
package io.coachify.dto.chat;

import java.time.Instant;

public record SeenAck(
  String chatRoomId,
  String byRole,           // "STUDENT" or "MENTOR"
  Instant seenUntil
) {}
