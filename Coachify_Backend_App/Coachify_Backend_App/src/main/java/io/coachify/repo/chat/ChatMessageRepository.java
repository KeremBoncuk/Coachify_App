package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

  /* newest → oldest */
  List<ChatMessage> findByChatRoomIdOrderBySentAtDesc(ObjectId chatRoomId, Pageable pageable);

  /* cursor-based (< before) */
  List<ChatMessage> findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(
    ObjectId chatRoomId, Instant before, Pageable pageable);
}
