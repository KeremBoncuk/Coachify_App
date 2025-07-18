package io.coachify.entity.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("mentors")
public class Mentor {
  @Id
  private ObjectId id;

  @Indexed(unique = true)
  private String fullName;

  private String email;        // Encrypted
  private String phoneNumber;  // Encrypted
  private String hashedPassword;

  private String school;
  private String department;
  private int placement;

  private SubjectArea area;
  private LocalDate birthDate;
  private String iban;
  private String notes;

  private boolean active;
  private LocalDate abandonmentDate;
}
