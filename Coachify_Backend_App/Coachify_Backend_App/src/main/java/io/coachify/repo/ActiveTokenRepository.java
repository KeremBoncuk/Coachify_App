package io.coachify.repo;

import io.coachify.entity.jwt.ActiveToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveTokenRepository extends MongoRepository<ActiveToken, String> {

  /** Used by JwtService when issuing a new token */
  void deleteByUserId(ObjectId userId);

  /** Used by AuthService.logout() */
  void deleteByUserIdAndTokenId(ObjectId userId, String tokenId);

  /** Used by JwtService.isTokenValid() */
  boolean existsByTokenId(String tokenId);
}
