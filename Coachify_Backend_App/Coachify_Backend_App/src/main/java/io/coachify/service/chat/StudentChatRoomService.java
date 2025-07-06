package io.coachify.service.chat;

import io.coachify.dto.chat.StudentChatRoomDTO;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.user.Mentor;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns all ACTIVE chat rooms for a given student,
 * enriched with the mentor’s full name.
 */
@Service
@RequiredArgsConstructor
public class StudentChatRoomService {

  private final ChatRoomRepository roomRepo;
  private final MentorRepository   mentorRepo;

  public List<StudentChatRoomDTO> getRoomsForStudent(ObjectId studentId) {
    return roomRepo.findByStudentIdAndIsActiveTrue(studentId).stream()
      .map(room -> mapToDto(room, studentId))
      .toList();
  }

  private StudentChatRoomDTO mapToDto(ChatRoom room, ObjectId studentId) {
    String mentorName = mentorRepo.findById(room.getMentorId())
      .map(Mentor::getFullName)
      .orElse("(unknown)");
    return new StudentChatRoomDTO(
      room.getId().toHexString(),
      studentId.toHexString(),
      room.getMentorId().toHexString(),
      mentorName,
      room.getCreatedAt(),
      room.isActive()
    );
  }
}
