package io.coachify.repo.chat;

import io.coachify.entity.chat.ChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, ObjectId> {

  List<ChatRoom> findByStudentIdAndIsActiveTrue(ObjectId studentId);

  List<ChatRoom> findByMentorIdAndIsActiveTrue(ObjectId mentorId);

  List<ChatRoom> findByStudentId(ObjectId studentId);

  List<ChatRoom> findByMentorId(ObjectId mentorId);

  List<ChatRoom> findByIsActiveTrue();

  Optional<ChatRoom> findByIdAndIsActiveTrue(ObjectId id);

  Optional<ChatRoom> findByStudentIdAndMentorId(ObjectId studentId, ObjectId mentorId);

  boolean existsById(ObjectId id);

  // Placeholder: implement filtering logic manually in service if needed
  default List<ChatRoom> findAllWithStudentMentorFiltered(String fullNameFilter, boolean onlyActive) {
    throw new UnsupportedOperationException("Custom filtering logic should be implemented in service layer or using @Query if needed.");
  }
}
