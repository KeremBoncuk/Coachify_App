package io.coachify.dto.chat.student;

import java.time.Instant;
import java.util.List;

public record StudentChatMessagesResponse(
  List<StudentChatMessageDTO> messages,
  Instant nextBefore,
  boolean hasMore
) {}
