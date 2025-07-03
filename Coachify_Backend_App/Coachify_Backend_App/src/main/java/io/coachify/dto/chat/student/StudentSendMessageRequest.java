package io.coachify.dto.chat.student;

import java.util.List;

public record StudentSendMessageRequest(
  String chatRoomId,
  String text,
  List<String> mediaUrls
) {}
