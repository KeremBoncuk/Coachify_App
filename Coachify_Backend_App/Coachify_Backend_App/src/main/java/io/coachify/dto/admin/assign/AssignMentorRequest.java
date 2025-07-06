package io.coachify.dto.admin.assign;

import lombok.Data;

@Data
public class AssignMentorRequest {
  private String studentId;  // ObjectId as hex string
  private String mentorId;   // ObjectId as hex string
}
