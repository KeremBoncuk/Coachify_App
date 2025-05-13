package io.coachify.entity.repo;

import io.coachify.entity.model.Student;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, ObjectId> {
  Optional<Student> findByEmail(String email);
}
