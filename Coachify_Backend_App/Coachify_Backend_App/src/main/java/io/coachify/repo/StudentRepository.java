package io.coachify.repo;

import io.coachify.entity.user.Student;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, ObjectId> {
  Optional<Student> findByFullName(String fullName);
}
