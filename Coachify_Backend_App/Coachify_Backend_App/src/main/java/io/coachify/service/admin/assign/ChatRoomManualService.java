package io.coachify.service.admin.assign;

import io.coachify.entity.chat.ChatRoom;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomManualService {

  private final ChatRoomRepository chatRoomRepository;

  /* ───── Manual admin operations ───── */

  public void activateChatRoom(ObjectId chatRoomId) {
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new NotFoundException("Chat-room not found"));

    if (!room.isActive()) {
      room.setActive(true);
      chatRoomRepository.save(room);
    }
    // If already active → idempotent no-op
  }

  public void inactivateChatRoom(ObjectId chatRoomId) {
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new NotFoundException("Chat-room not found"));

    if (room.isActive()) {
      room.setActive(false);
      chatRoomRepository.save(room);
    }
    // If already inactive → idempotent no-op
  }
}
