package io.coachify.entity.chat;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A single chat room for one student–mentor pair.
 * The compound unique index guarantees **at most one ACTIVE room**
 * for any given pair, eliminating race conditions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("chat_rooms")
@CompoundIndex(
  name     = "pair_active_unique",
  unique   = true,
  def      = "{ 'studentId': 1, 'mentorId': 1, 'isActive': 1 }"
)
public class ChatRoom {

  @Id
  private ObjectId id;

  private ObjectId studentId;
  private ObjectId mentorId;

  private boolean isActive;
  private Instant createdAt;
}
