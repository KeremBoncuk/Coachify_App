package io.coachify.dto.admin.list;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
  private String id;
  private String fullName;
  private String email;
  private String phoneNumber;
}
