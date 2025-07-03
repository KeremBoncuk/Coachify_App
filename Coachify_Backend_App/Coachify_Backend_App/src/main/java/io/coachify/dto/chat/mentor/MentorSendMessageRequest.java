package io.coachify.dto.chat.mentor;

import java.util.List;

public record MentorSendMessageRequest(
  String chatRoomId,
  String text,
  List<String> mediaUrls
) {}
