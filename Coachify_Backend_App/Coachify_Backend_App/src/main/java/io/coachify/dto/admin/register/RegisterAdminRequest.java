package io.coachify.dto.admin.register;

import lombok.Data;

@Data
public class RegisterAdminRequest {
  private String fullName;
  private String email;
  private String phoneNumber;
  private String password;
}
