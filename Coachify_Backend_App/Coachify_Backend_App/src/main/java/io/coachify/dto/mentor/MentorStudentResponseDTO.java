package io.coachify.dto.mentor;

import io.coachify.entity.user.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MentorStudentResponseDTO {
  private String id;
  private String fullName;
  private String email;
  private String phoneNumber;
  private PaymentStatus paymentStatus;
  private LocalDate purchaseDate;
  private LocalDate subscriptionStartDate;
  private LocalDate nextPaymentDate;
  private boolean active;
}
