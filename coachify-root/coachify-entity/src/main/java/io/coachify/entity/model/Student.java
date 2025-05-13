package io.coachify.entity.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "students")
public class Student extends User {
  private UserRole role = UserRole.STUDENT;
  private ObjectId coachId;
  private ObjectId adminId;
  private StudentInfo studentInfo;
}
