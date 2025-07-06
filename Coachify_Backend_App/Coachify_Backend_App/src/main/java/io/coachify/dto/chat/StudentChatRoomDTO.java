// StudentChatRoomDTO.java
package io.coachify.dto.chat;

import java.time.Instant;

/** Row item shown in Student UI (just needs mentorId for now). */
public record StudentChatRoomDTO(
  String id,
  String studentId,
  String mentorId,
  String mentorFullName,
  Instant createdAt,
  boolean isActive
) {}
