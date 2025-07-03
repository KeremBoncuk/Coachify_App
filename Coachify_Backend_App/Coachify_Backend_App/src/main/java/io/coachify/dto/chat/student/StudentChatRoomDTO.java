package io.coachify.dto.chat.student;

import java.time.Instant;

public record StudentChatRoomDTO(
  String id,
  String studentId,
  String mentorId,
  Instant createdAt,
  boolean active
) {}
