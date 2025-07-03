package io.coachify.entity.chat;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("chat_messages")
public class ChatMessage {
  @Id
  private ObjectId id;

  private ObjectId chatRoomId;
  private ObjectId senderId;
  private String senderRole; // "STUDENT", "MENTOR", "ADMIN"

  private String text;
  private List<String> mediaUrls;

  private SeenStatus seenStatus;
  private Instant seenAt;
  private Instant sentAt;
}
