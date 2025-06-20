package io.coachify.entity.chat;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("chat_rooms")
public class ChatRoom {
  @Id
  private ObjectId id;

  private ObjectId studentId;
  private ObjectId mentorId;

  private boolean isActive;
  private Instant createdAt;
}
