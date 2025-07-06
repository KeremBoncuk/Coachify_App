package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, ObjectId> {

  /* ────────────── Lookup helpers ────────────── */

  // Active rooms
  List<ChatRoom> findByStudentIdAndIsActiveTrue(ObjectId studentId);
  List<ChatRoom> findByMentorIdAndIsActiveTrue(ObjectId mentorId);
  List<ChatRoom> findByIsActiveTrue();

  // All rooms (any state)
  List<ChatRoom> findByStudentId(ObjectId studentId);
  List<ChatRoom> findByMentorId(ObjectId mentorId);

  // By ID helpers
  Optional<ChatRoom> findByIdAndIsActiveTrue(ObjectId id);

  // By student-mentor pair
  Optional<ChatRoom> findByStudentIdAndMentorId(ObjectId studentId, ObjectId mentorId);
  Optional<ChatRoom> findByStudentIdAndMentorIdAndIsActiveTrue(ObjectId studentId, ObjectId mentorId);

  /* ────────────── Misc ────────────── */

  boolean existsById(ObjectId id);

  // Placeholder for custom filtering (optional @Query implementation)
  default List<ChatRoom> findAllWithStudentMentorFiltered(String fullNameFilter, boolean onlyActive) {
    throw new UnsupportedOperationException(
      "Implement custom filtering logic in service layer or with @Query if required."
    );
  }
}
