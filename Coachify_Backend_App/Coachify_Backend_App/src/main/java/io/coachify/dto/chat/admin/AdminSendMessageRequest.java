package io.coachify.dto.chat.admin;

import java.util.List;

public record AdminSendMessageRequest(
  String text,
  List<String> mediaUrls
) {}
