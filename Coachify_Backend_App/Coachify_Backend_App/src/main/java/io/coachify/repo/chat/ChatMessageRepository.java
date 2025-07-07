package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

/**
 * Mongo repository for ChatMessage.
 * Both ascending and descending helpers are provided,
 * as well as cursor-based paging.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

  /* ─────────── Full dumps ─────────── */

  /* newest → oldest */
  List<ChatMessage> findByChatRoomIdOrderBySentAtDesc(ObjectId chatRoomId);

  /* oldest → newest  (used by student / mentor views) */
  List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(ObjectId chatRoomId);

  /* ─────────── Paged, newest→oldest ─────────── */

  List<ChatMessage> findByChatRoomIdOrderBySentAtDesc(
    ObjectId chatRoomId, Pageable pageable);

  /* EXCLUSIVE  (< before) */
  List<ChatMessage> findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(
    ObjectId chatRoomId, Instant before, Pageable pageable);

  /* INCLUSIVE (<= before) — kept for older code paths */
  List<ChatMessage> findByChatRoomIdAndSentAtLessThanEqualOrderBySentAtDesc(
    ObjectId chatRoomId, Instant before, Pageable pageable);

  /* ─────────── Seen-status convenience queries (unchanged) ─────────── */

  List<ChatMessage>
  findByChatRoomIdAndSenderRoleAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
    ObjectId chatRoomId, String senderRole, Instant until);

  List<ChatMessage>
  findByChatRoomIdAndSenderRoleAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
    ObjectId chatRoomId, String senderRole, Instant until);

  List<ChatMessage>
  findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
    ObjectId chatRoomId, List<String> senderRoles, Instant until);

  List<ChatMessage>
  findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
    ObjectId chatRoomId, List<String> senderRoles, Instant until);

  /* ─────────── Convenience wrapper (optional) ─────────── */

  default List<ChatMessage> findLimitedMessages(
    ObjectId chatRoomId, Instant before, int limit) {

    Pageable page = PageRequest.of(0, limit);
    return (before == null)
      ? findByChatRoomIdOrderBySentAtDesc(chatRoomId, page)
      : findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, before, page);
  }
}
