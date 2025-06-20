package io.coachify.repo;

import io.coachify.entity.jwt.ActiveToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveTokenRepository extends MongoRepository<ActiveToken, ObjectId> {

  // 🗑️ Delete all active tokens by userId
  void deleteByUserId(ObjectId userId);

  // ✅ Check if a token with the given tokenId exists
  boolean existsByTokenId(String tokenId);

  // (Optional) For logout: delete token by tokenId
  void deleteByTokenId(String tokenId);
}
