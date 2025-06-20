package io.coachify.entity.jwt;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("active_tokens")
public class ActiveToken {

  @Id
  private ObjectId id;

  @Indexed
  private ObjectId userId; // Refers to Student, Mentor, or Admin

  @Indexed(unique = true)
  private String tokenId;  // UUID inside the JWT

  private UserRole role;   // Optional: to aid in audits

  private Instant issuedAt;
  private Instant expiresAt;
}
