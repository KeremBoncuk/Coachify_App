// src/main/java/io/coachify/dto/chat/mentor/MentorChatRoomDTO.java
package io.coachify.dto.chat.mentor;

import java.time.Instant;

public record MentorChatRoomDTO(
  String id,
  String studentId,
  String mentorId,
  Instant createdAt,
  boolean active
) {}
