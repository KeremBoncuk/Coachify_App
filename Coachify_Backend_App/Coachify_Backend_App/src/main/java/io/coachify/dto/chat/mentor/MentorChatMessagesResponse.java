package io.coachify.dto.chat.mentor;

import java.time.Instant;
import java.util.List;

public record MentorChatMessagesResponse(
  List<MentorChatMessageDTO> messages,
  Instant nextBefore,
  boolean hasMore
) {}
