package io.coachify.dto.chat;

import java.time.Instant;

/**
 * Admin-side “chat-room” payload.
 *
 *  • mentorFullName / studentFullName are now included so the
 *    frontend can render the list without extra look-ups.
 */
public record ChatRoomAdminDTO(
  String id,
  String studentId,
  String mentorId,
  String mentorFullName,
  String studentFullName,
  boolean isActive,
  Instant createdAt
) {}
