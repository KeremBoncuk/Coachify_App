package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

  /* ─────────────────────────── Standard fetch helpers ─────────────────────────── */

  List<ChatMessage> findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(
    ObjectId chatRoomId,
    Instant before,
    Pageable pageable);

  List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(ObjectId chatRoomId);
  List<ChatMessage> findByChatRoomIdOrderBySentAtDesc(ObjectId chatRoomId);

  /* ──────────────────── “mark-as-seen” convenience queries ───────────────────── */

  /* ➊ Single-role helpers (kept for backward compatibility) */
  List<ChatMessage> findByChatRoomIdAndSenderRoleAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
    ObjectId chatRoomId,
    String senderRole,
    Instant until);

  List<ChatMessage> findByChatRoomIdAndSenderRoleAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
    ObjectId chatRoomId,
    String senderRole,
    Instant until);

  /* ➋ Multi-role helpers (NEW) */
  List<ChatMessage> findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
    ObjectId chatRoomId,
    List<String> senderRoles,
    Instant until);

  List<ChatMessage> findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
    ObjectId chatRoomId,
    List<String> senderRoles,
    Instant until);

  /* ─────────────── Thin wrapper used by “/limited” pagination endpoint ─────────────── */

  default List<ChatMessage> findLimitedMessages(ObjectId chatRoomId, Instant before, int limit) {
    Pageable page = Pageable.ofSize(limit);
    Instant cutoff = (before != null) ? before : Instant.now();
    return findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, cutoff, page);
  }
}
