package io.coachify.dto.admin;

import io.coachify.entity.user.MentorChangeRecord;
import io.coachify.entity.user.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
  private String id;
  private String fullName;
  private String email;
  private String phoneNumber;

  private String assignedMentor; // ObjectId as hex string (nullable)
  private LocalDate purchaseDate;
  private LocalDate subscriptionStartDate;
  private LocalDate nextPaymentDate;

  private PaymentStatus paymentStatus;
  private String notes;

  private List<MentorChangeRecord> mentorChangeHistory;

  private boolean active;
  private LocalDate abandonmentDate;
}
