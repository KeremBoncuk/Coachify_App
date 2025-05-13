package io.coachify.entity.repo;

import io.coachify.entity.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
}
