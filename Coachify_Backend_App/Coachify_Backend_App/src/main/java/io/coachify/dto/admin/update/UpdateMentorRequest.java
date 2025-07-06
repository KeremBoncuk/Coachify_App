package io.coachify.dto.admin.update;

import io.coachify.entity.user.SubjectArea;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMentorRequest {
  private String id;  // ObjectId as hex string

  private String fullName;
  private String email;
  private String phoneNumber;

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
