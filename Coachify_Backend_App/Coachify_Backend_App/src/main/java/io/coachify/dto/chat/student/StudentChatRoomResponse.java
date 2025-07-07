package io.coachify.dto.chat.student;

import org.bson.types.ObjectId;
import java.time.Instant;

public record StudentChatRoomResponse(
  String chatRoomId,
  String mentorId,
  boolean isActive,
  Instant createdAt
) {}
