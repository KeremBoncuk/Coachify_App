package io.coachify.entity.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "admins")
public class Admin extends User {

  private UserRole role = UserRole.ADMIN;

  private List<ObjectId> managedStudentIds = new ArrayList<>();

  private List<ObjectId> managedCoachIds = new ArrayList<>();
}
