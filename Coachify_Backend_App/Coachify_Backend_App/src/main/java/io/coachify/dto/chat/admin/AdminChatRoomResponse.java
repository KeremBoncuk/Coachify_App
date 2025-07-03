package io.coachify.dto.chat.admin;

import java.time.Instant;
import org.bson.types.ObjectId;

public record AdminChatRoomResponse(
  String id,
  String studentFullName,
  String mentorFullName,
  boolean isActive,
  Instant createdAt
) {}
