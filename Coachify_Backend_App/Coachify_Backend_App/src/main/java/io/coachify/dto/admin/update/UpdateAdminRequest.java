package io.coachify.dto.admin.update;

import lombok.Data;

@Data
public class UpdateAdminRequest {
  private String id;  // ObjectId as hex string
  private String fullName;
  private String email;
  private String phoneNumber;
}
