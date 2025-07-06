// ChatSeenPublisher.java  (service/chat)
package io.coachify.service.chat;

import io.coachify.dto.chat.SeenAck;
import io.coachify.messaging.ChatTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSeenPublisher {

  private final SimpMessagingTemplate broker;

  public void publish(String roomHex, String byRole, java.time.Instant until) {
    broker.convertAndSend(ChatTopic.room(roomHex) + "/seen",
      new SeenAck(roomHex, byRole, until));
  }
}
