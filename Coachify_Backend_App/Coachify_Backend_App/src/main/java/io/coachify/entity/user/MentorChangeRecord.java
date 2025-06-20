package io.coachify.entity.user;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorChangeRecord {
  private ObjectId mentorId;
  private Instant assignedAt;
  private Instant unassignedAt; // null if still assigned
}
