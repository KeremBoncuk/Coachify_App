package io.coachify.service.chat;

import io.coachify.dto.chat.ChatRoomAdminDTO;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.exception.BadRequestException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomAdminService {

  private final ChatRoomRepository roomRepo;
  private final StudentRepository  studentRepo;
  private final MentorRepository   mentorRepo;

  /* ──────────────── Create ─────────────────────────────── */
  public ChatRoomAdminDTO createRoom(String studentIdHex, String mentorIdHex) {

    ObjectId studentId = new ObjectId(studentIdHex);
    ObjectId mentorId  = new ObjectId(mentorIdHex);

    Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));
    Mentor mentor = mentorRepo.findById(mentorId)
            .orElseThrow(() -> new NotFoundException("Mentor not found"));

    if (!mentorId.equals(student.getAssignedMentor()))
      throw new BadRequestException("Mentor not assigned to this student");

    roomRepo.findByStudentIdAndMentorId(studentId, mentorId)
            .ifPresent(r -> { throw new BadRequestException("Chat room already exists"); });

    ChatRoom room = new ChatRoom();
    room.setStudentId(studentId);
    room.setMentorId(mentorId);
    room.setActive(true);
    room.setCreatedAt(Instant.now());

    return toDto(roomRepo.save(room), student, mentor);
  }

  /* ──────────────── Update active flag ─────────────────── */
  public void setActive(String roomIdHex, boolean active) {
    ChatRoom room = roomRepo.findById(new ObjectId(roomIdHex))
            .orElseThrow(() -> new NotFoundException("Room not found"));
    room.setActive(active);
    roomRepo.save(room);
  }

  /* ──────────────── List / filter ──────────────────────── */
  public List<ChatRoomAdminDTO> list(Boolean onlyActive,
                                     String studentIdHex,
                                     String mentorIdHex) {

    List<ChatRoom> rooms;

    if (studentIdHex != null) {
      ObjectId sid = new ObjectId(studentIdHex);
      rooms = roomRepo.findByStudentId(sid);
    } else if (mentorIdHex != null) {
      ObjectId mid = new ObjectId(mentorIdHex);
      rooms = roomRepo.findByMentorId(mid);
    } else {
      rooms = roomRepo.findAll();
    }

    /* optional active / inactive filter */
    rooms = rooms.stream()
            .filter(r -> onlyActive == null || r.isActive() == onlyActive)
            .toList();

    if (rooms.isEmpty()) return List.of();

    /* ---- batch-fetch mentors & students to avoid N+1 ---- */
    Set<ObjectId> studentIds = rooms.stream()
            .map(ChatRoom::getStudentId)
            .collect(Collectors.toSet());
    Set<ObjectId> mentorIds  = rooms.stream()
            .map(ChatRoom::getMentorId)
            .collect(Collectors.toSet());

    Map<ObjectId, Student> studentMap = studentRepo.findAllById(studentIds)
            .stream()
            .collect(Collectors.toMap(Student::getId, Function.identity()));

    Map<ObjectId, Mentor> mentorMap = mentorRepo.findAllById(mentorIds)
            .stream()
            .collect(Collectors.toMap(Mentor::getId, Function.identity()));

    /* ---- map to DTOs ---- */
    return rooms.stream()
            .map(r -> {
              Student s = studentMap.get(r.getStudentId());   // may be null if deleted
              Mentor  m = mentorMap.get(r.getMentorId());

              String studentName = s != null ? s.getFullName() : "(student)";
              String mentorName  = m != null ? m.getFullName() : "(mentor)";

              return toDto(r, studentName, mentorName);
            })
            .toList();
  }

  /* ──────────────── Helpers / mappers ──────────────────── */

  private ChatRoomAdminDTO toDto(ChatRoom room,
                                 Student student,
                                 Mentor mentor) {

    return new ChatRoomAdminDTO(
            room.getId().toHexString(),
            student.getId().toHexString(),
            mentor.getId().toHexString(),
            mentor.getFullName(),
            student.getFullName(),
            room.isActive(),
            room.getCreatedAt()
    );
  }

  private ChatRoomAdminDTO toDto(ChatRoom room,
                                 String studentFullName,
                                 String mentorFullName) {

    return new ChatRoomAdminDTO(
            room.getId().toHexString(),
            room.getStudentId().toHexString(),
            room.getMentorId().toHexString(),
            mentorFullName,
            studentFullName,
            room.isActive(),
            room.getCreatedAt()
    );
  }
}
