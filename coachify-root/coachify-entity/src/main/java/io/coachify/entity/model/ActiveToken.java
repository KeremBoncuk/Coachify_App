package io.coachify.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "active_tokens")
public class ActiveToken {
  @Id
  private ObjectId id;
  @Indexed(unique = true)
  private String tokenId;
  @Indexed
  private ObjectId userId;
  private Instant expiration;
  private Instant createdAt;

  public ActiveToken(String tokenId, ObjectId userId, Instant expiration) {
    this.tokenId = tokenId;
    this.userId = userId;
    this.expiration = expiration;
    this.createdAt = Instant.now();
  }
}
