package io.coachify.entity.repo;

import io.coachify.entity.model.Coach;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CoachRepository extends MongoRepository<Coach, ObjectId> {
  Optional<Coach> findByEmail(String email);
}
