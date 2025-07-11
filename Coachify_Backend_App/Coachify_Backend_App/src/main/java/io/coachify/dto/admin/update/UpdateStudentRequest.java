package io.coachify.dto.admin.update;

import io.coachify.entity.user.MentorChangeRecord;
import io.coachify.entity.user.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateStudentRequest {
  private String id;  // ObjectId as hex string

  private String fullName;
  private String email;
  private String phoneNumber;

  // REMOVED: assignedMentor (mentor is now assigned via a separate endpoint)

  private LocalDate purchaseDate;
  private LocalDate subscriptionStartDate;
  private LocalDate nextPaymentDate;

  private PaymentStatus paymentStatus;
  private String notes;

  private List<MentorChangeRecord> mentorChangeHistory;

  private boolean active;
  private LocalDate abandonmentDate;
}
