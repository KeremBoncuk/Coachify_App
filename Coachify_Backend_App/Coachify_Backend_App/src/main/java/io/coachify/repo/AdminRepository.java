package io.coachify.repo;

import io.coachify.entity.user.Admin;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, ObjectId> {
  Optional<Admin> findByFullName(String fullName);
}
