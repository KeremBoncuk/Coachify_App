package io.coachify.service.chat;

import io.coachify.dto.chat.MentorChatRoomDTO;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.user.Student;
import io.coachify.repo.StudentRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns all ACTIVE chat rooms for a given mentor,
 * enriched with the student’s full name.
 */
@Service
@RequiredArgsConstructor
public class MentorChatRoomService {

  private final ChatRoomRepository roomRepo;
  private final StudentRepository  studentRepo;

  public List<MentorChatRoomDTO> getRoomsForMentor(ObjectId mentorId) {
    return roomRepo.findByMentorIdAndIsActiveTrue(mentorId).stream()
      .map(room -> mapToDto(room, mentorId))
      .toList();
  }

  private MentorChatRoomDTO mapToDto(ChatRoom room, ObjectId mentorId) {
    String studentName = studentRepo.findById(room.getStudentId())
      .map(Student::getFullName)
      .orElse("(unknown)");
    return new MentorChatRoomDTO(
      room.getId().toHexString(),
      room.getStudentId().toHexString(),
      mentorId.toHexString(),
      studentName,
      room.getCreatedAt(),
      room.isActive()
    );
  }
}
