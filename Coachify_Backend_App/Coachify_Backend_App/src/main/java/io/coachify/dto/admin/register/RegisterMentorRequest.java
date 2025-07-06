package io.coachify.dto.admin.register;

import io.coachify.entity.user.SubjectArea;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterMentorRequest {
  private String fullName;
  private String email;
  private String phoneNumber;
  private String password;

  private String school;
  private String department;
  private int placement;

  private SubjectArea area;
  private LocalDate birthDate;
  private String iban;
  private String notes;
}
