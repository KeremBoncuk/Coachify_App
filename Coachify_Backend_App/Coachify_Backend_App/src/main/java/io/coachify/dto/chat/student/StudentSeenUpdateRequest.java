package io.coachify.dto.chat.student;

import java.time.Instant;

public record StudentSeenUpdateRequest(
  String chatRoomId,
  Instant seenUntil
) {}
