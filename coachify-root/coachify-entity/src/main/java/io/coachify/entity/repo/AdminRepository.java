package io.coachify.entity.repo;

import io.coachify.entity.model.Admin;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, ObjectId> {
  Optional<Admin> findByEmail(String email);
}
