package io.coachify.dto.admin.list;

import io.coachify.entity.user.SubjectArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorResponseDTO {
  private String id;
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
