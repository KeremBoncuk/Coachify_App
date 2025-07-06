package io.coachify.repo;

import io.coachify.entity.user.Mentor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends MongoRepository<Mentor, ObjectId> {

  Optional<Mentor> findByFullName(String fullName);

  /* NEW — filter by active */
  List<Mentor> findByActive(Boolean active);
}
