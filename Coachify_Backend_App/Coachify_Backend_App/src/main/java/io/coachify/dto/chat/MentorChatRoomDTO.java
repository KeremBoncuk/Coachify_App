// MentorChatRoomDTO.java
package io.coachify.dto.chat;

import java.time.Instant;

/** Row item shown in Mentor UI (needs student’s name). */
public record MentorChatRoomDTO(
  String id,
  String studentId,
  String mentorId,
  String studentFullName,
  Instant createdAt,
  boolean isActive
) {}
