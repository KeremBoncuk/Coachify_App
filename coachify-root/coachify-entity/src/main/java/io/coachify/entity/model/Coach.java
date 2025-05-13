package io.coachify.entity.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "coaches")
public class Coach extends User {
  private UserRole role = UserRole.COACH;
  private List<ObjectId> activeStudentIds;
  private List<ObjectId> totalStudentIds;
  private ObjectId adminId;
  private CoachInfo coachInfo;
}
