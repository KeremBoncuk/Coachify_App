package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, ObjectId> {

  /* Pair look-ups */
  Optional<ChatRoom> findByStudentIdAndMentorId(ObjectId studentId, ObjectId mentorId);
  Optional<ChatRoom> findByStudentIdAndMentorIdAndIsActiveTrue(ObjectId studentId, ObjectId mentorId);

  /* Role-scoped room lists */
  List<ChatRoom> findByStudentIdAndIsActiveTrue(ObjectId studentId);
  List<ChatRoom> findByMentorIdAndIsActiveTrue(ObjectId mentorId);

  List<ChatRoom> findByStudentId(ObjectId studentId);
  List<ChatRoom> findByMentorId(ObjectId mentorId);     // ← fixed param name

  Optional<ChatRoom> findByIdAndIsActiveTrue(ObjectId id);

  /* Misc helpers */
  boolean existsById(ObjectId id);

  /* Placeholder for future @Query */
  default List<ChatRoom> findAllWithStudentMentorFiltered(String fullNameFilter, boolean onlyActive) {
    throw new UnsupportedOperationException(
      "Implement custom filtering logic in service layer or with @Query if required."
    );
  }
}
