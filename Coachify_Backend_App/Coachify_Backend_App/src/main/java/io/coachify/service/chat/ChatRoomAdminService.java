package io.coachify.service.chat;

import io.coachify.dto.chat.admin.AdminChatRoomResponse;
import io.coachify.dto.chat.admin.CreateChatRoomRequest;
import io.coachify.dto.chat.admin.UpdateChatRoomRequest;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.exception.BadRequestException;
import io.coachify.exception.ConflictException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomAdminService {

  private final ChatRoomRepository chatRoomRepository;
  private final StudentRepository studentRepository;
  private final MentorRepository mentorRepository;

  public List<AdminChatRoomResponse> getChatRooms(Boolean onlyActive, String studentId, String mentorId) {
    if (studentId != null && mentorId != null) {
      throw new BadRequestException("Only one of studentId or mentorId can be provided.");
    }

    List<ChatRoom> rooms;

    if (studentId != null) {
      ObjectId studentObjectId = new ObjectId(studentId);
      if (!studentRepository.existsById(studentObjectId)) {
        throw new NotFoundException("Student not found.");
      }
      rooms = chatRoomRepository.findByStudentId(studentObjectId);
    } else if (mentorId != null) {
      ObjectId mentorObjectId = new ObjectId(mentorId);
      if (!mentorRepository.existsById(mentorObjectId)) {
        throw new NotFoundException("Mentor not found.");
      }
      rooms = chatRoomRepository.findByMentorId(mentorObjectId);
    } else {
      rooms = chatRoomRepository.findAll();
    }

    return rooms.stream()
      .filter(room -> onlyActive == null || room.isActive() == onlyActive)
      .map(this::toChatRoomResponse)
      .toList();
  }

  public AdminChatRoomResponse getRoomDetails(ObjectId chatRoomId) {
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new NotFoundException("Chat room not found"));
    return toChatRoomResponse(room);
  }

  public AdminChatRoomResponse createChatRoom(CreateChatRoomRequest request) {
    ObjectId studentId = new ObjectId(request.studentId());
    ObjectId mentorId = new ObjectId(request.mentorId());

    Student student = studentRepository.findById(studentId)
      .orElseThrow(() -> new BadRequestException("Student not found"));
    Mentor mentor = mentorRepository.findById(mentorId)
      .orElseThrow(() -> new BadRequestException("Mentor not found"));

    if (!mentorId.equals(student.getAssignedMentor())) {
      throw new BadRequestException("Mentor is not assigned to this student");
    }

    chatRoomRepository.findByStudentIdAndMentorId(studentId, mentorId).ifPresent(existing -> {
      if (existing.isActive()) {
        throw new ConflictException("Chat room already exists between this student and mentor");
      } else {
        throw new ConflictException("Chat room exists but is inactive. Consider reactivating it.");
      }
    });

    ChatRoom newRoom = new ChatRoom();
    newRoom.setStudentId(studentId);
    newRoom.setMentorId(mentorId);
    newRoom.setActive(true);
    newRoom.setCreatedAt(Instant.now());

    return toChatRoomResponse(chatRoomRepository.save(newRoom));
  }

  public void updateChatRoomStatus(ObjectId chatRoomId, boolean active) {
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new NotFoundException("Chat room not found"));
    room.setActive(active);
    chatRoomRepository.save(room);
  }

  private AdminChatRoomResponse toChatRoomResponse(ChatRoom room) {
    String studentName = studentRepository.findById(room.getStudentId())
      .map(Student::getFullName)
      .orElse("Unknown Student");

    String mentorName = mentorRepository.findById(room.getMentorId())
      .map(Mentor::getFullName)
      .orElse("Unknown Mentor");

    return new AdminChatRoomResponse(
      room.getId().toHexString(),
      studentName,
      mentorName,
      room.isActive(),
      room.getCreatedAt()
    );
  }
}
