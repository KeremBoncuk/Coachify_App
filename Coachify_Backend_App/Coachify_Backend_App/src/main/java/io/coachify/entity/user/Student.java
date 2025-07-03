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
@Document("students")
public class Student {
  @Id
  private ObjectId id;

  @Indexed(unique = true)
  private String fullName;

  private String email;        // Encrypted
  private String phoneNumber;  // Encrypted
  private String hashedPassword;

  private ObjectId assignedMentor;

  private LocalDate purchaseDate;
  private LocalDate subscriptionStartDate;
  private LocalDate nextPaymentDate;

  private PaymentStatus paymentStatus;
  private String notes;

  private List<MentorChangeRecord> mentorChangeHistory;

  private boolean active;
  private LocalDate abandonmentDate;
}
