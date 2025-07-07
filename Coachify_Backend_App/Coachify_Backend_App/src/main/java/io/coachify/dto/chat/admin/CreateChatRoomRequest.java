package io.coachify.dto.chat.admin;

import org.bson.types.ObjectId;

public record CreateChatRoomRequest(
  String studentId,
  String mentorId
) {}
