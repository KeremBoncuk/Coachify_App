package io.coachify.service.chat;

import io.coachify.dto.chat.ChatMessageResponse;
import io.coachify.entity.chat.ChatMessage;
import io.coachify.messaging.ChatTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/** Publishes freshly-saved chat messages to /topic/chat/<roomId>. */
@Service
@RequiredArgsConstructor
public class ChatMessagePublisher {

  private final SimpMessagingTemplate broker;

  public void publish(ChatMessage entity) {
    ChatMessageResponse dto = toDto(entity);
    broker.convertAndSend(ChatTopic.room(entity.getChatRoomId().toHexString()), dto);
  }

  private ChatMessageResponse toDto(ChatMessage m) {
    return new ChatMessageResponse(
      m.getId().toHexString(),
      m.getChatRoomId().toHexString(),
      m.getSenderId().toHexString(),
      m.getSenderRole(),
      m.getText(),
      m.getMediaUrls(),
      m.getSeenStatus(),
      m.getSeenAt(),
      m.getSentAt());
  }
}
