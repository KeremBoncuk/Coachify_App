package io.coachify.config.mongo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveTokenTTLConfig {

  private final MongoTemplate mongoTemplate;

  @PostConstruct
  public void setupTTLIndex() {
    mongoTemplate.indexOps("active_tokens") // Use the actual collection name
      .ensureIndex(new Index()
        .on("expiresAt", org.springframework.data.domain.Sort.Direction.ASC)
        .expire(0)); // expires exactly at the Instant set in the field
  }
}
