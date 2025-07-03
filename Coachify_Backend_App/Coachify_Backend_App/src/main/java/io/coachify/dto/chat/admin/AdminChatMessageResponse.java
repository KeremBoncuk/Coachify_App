package io.coachify.dto.chat.admin;

import java.time.Instant;
import java.util.List;
import org.bson.types.ObjectId;

public record AdminChatMessageResponse(
  String id,
  String chatRoomId,
  String senderId,
  String senderRole,
  String text,
  List<String> mediaUrls,
  Instant sentAt,
  Instant seenAt
) {}
