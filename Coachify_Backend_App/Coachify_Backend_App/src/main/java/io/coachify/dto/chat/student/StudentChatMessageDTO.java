package io.coachify.dto.chat.student;

import io.coachify.entity.chat.SeenStatus;
import java.time.Instant;
import java.util.List;

public record StudentChatMessageDTO(
  String id,
  String senderId,
  String senderRole,
  String senderFullName,  // ✅ Added
  String text,
  List<String> mediaUrls,
  SeenStatus seenStatus,
  Instant seenAt,
  Instant sentAt
) {}
