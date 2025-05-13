package io.coachify.entity.repo;

import io.coachify.entity.model.ActiveToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ActiveTokenRepository extends MongoRepository<ActiveToken, ObjectId> {

  Optional<ActiveToken> findByTokenId(String tokenId);

  List<ActiveToken> findAllByUserId(ObjectId userId);

  void deleteAllByUserId(ObjectId userId);
}
