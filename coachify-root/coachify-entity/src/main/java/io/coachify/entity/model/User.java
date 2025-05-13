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
@Document(collection = "users")
public class User {
  @Id
  private ObjectId id;
  @Indexed(unique = true)
  private String username;
  private String name;
  private String surname;
  @Indexed(unique = true)
  private String email;
  @Indexed(unique = true)
  private String phoneNumber;
  private boolean active;
  private String password;
  private Instant createdAt;
  private int tokenVersion;
}
