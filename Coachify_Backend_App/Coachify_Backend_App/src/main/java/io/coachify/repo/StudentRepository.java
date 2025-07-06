package io.coachify.repo;

import io.coachify.entity.user.Student;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, ObjectId> {

  Optional<Student> findByFullName(String fullName);

  /* NEW — filter by active */
  List<Student> findByActive(Boolean active);

  /* already existed earlier */
  List<Student> findByAssignedMentor(ObjectId assignedMentor);
}
