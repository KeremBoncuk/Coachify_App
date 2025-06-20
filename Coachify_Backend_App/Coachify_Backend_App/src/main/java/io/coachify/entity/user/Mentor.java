package io.coachify.entity.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("mentors")
public class Mentor {
  @Id
  private ObjectId id;

  private String name;
  private String surname;

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

  private List<ObjectId> assignedStudents;

  private boolean isActive;
  private LocalDate abandonmentDate;
}
