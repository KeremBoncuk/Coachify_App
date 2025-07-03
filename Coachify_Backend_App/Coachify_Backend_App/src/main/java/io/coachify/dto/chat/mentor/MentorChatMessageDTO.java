package io.coachify.dto.chat.mentor;

import io.coachify.entity.chat.SeenStatus;

import java.time.Instant;
import java.util.List;

public record MentorChatMessageDTO(
  String messageId,
  String senderId,
  String senderRole,
  String text,
  List<String> mediaUrls,
  SeenStatus seenStatus,
  Instant seenAt,
  Instant sentAt
) {}
