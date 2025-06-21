package io.coachify.repo;

import io.coachify.entity.user.Mentor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MentorRepository extends MongoRepository<Mentor, ObjectId> {
  Optional<Mentor> findByFullName(String fullName);
}
