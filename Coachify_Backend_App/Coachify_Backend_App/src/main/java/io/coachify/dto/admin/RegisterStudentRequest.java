package io.coachify.dto.admin;

import io.coachify.entity.user.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterStudentRequest {
  private String fullName;
  private String email;
  private String phoneNumber;
  private String password;

  private LocalDate purchaseDate;
  private LocalDate subscriptionStartDate;
  private LocalDate nextPaymentDate;

  private PaymentStatus paymentStatus;
  private String notes;
}
