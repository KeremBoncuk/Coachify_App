package io.coachify.dto.chat.admin;

import java.time.Instant;
import java.util.List;

/**
 * Wrapper for a page of chat messages plus pagination cursor.
 */
public record AdminChatMessagePage(
  List<AdminChatMessageResponse> messages,
  boolean hasMore,
  Instant nextBefore          // null when hasMore == false
) {}
