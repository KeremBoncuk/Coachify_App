package io.coachify.entity.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("admins")
public class Admin {
  @Id
  private ObjectId id;

  @Indexed(unique = true)
  private String fullName;

  private String email;        // Encrypted
  private String phoneNumber;  // Encrypted
  private String hashedPassword;
}
