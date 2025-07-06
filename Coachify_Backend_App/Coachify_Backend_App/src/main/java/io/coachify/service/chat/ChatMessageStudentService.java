package io.coachify.service.chat;

import io.coachify.dto.chat.student.*;
import io.coachify.entity.chat.*;
import io.coachify.entity.user.Student;
import io.coachify.repo.StudentRepository;
import io.coachify.repo.chat.ChatMessageRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageStudentService {

  private final ChatRoomRepository    roomRepo;
  private final ChatMessageRepository msgRepo;
  private final StudentRepository     studentRepo;

  /* 1 ─ SEND */
  public void sendMessageByStudent(ObjectId studentId, StudentSendMessageRequest req) {

    Student student = studentRepo.findById(studentId)
      .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    ObjectId roomId = new ObjectId(req.chatRoomId());
    ChatRoom room   = roomRepo.findById(roomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

    if (!room.isActive())                          throw new IllegalStateException("Chat room inactive");
    if (!room.getStudentId().equals(studentId))    throw new SecurityException("Student not in this room");

    boolean textEmpty = !StringUtils.hasText(req.text());
    boolean noMedia   = req.mediaUrls() == null || req.mediaUrls().isEmpty();
    if (textEmpty && noMedia)
      throw new IllegalArgumentException("Message must contain text or media");

    ChatMessage m = new ChatMessage();
    m.setChatRoomId(roomId);
    m.setSenderId(studentId);
    m.setSenderRole("STUDENT");
    m.setText(req.text());
    m.setMediaUrls(noMedia ? List.of() : req.mediaUrls());
    m.setSentAt(Instant.now());
    m.setSeenStatus(new SeenStatus(true, false));      // student sees own message

    msgRepo.save(m);
  }

  /* 2 ─ ACTIVE ROOMS */
  public List<StudentChatRoomDTO> getActiveChatRoomsForStudent(ObjectId studentId) {
    return roomRepo.findByStudentIdAndIsActiveTrue(studentId).stream()
      .map(r -> new StudentChatRoomDTO(
        r.getId().toHexString(),
        r.getStudentId().toHexString(),
        r.getMentorId().toHexString(),
        r.getCreatedAt(),
        r.isActive()))
      .toList();
  }

  /* 3 ─ PAGINATED (exclusive cursor) */
  public StudentChatMessagesResponse getMessages(
    ObjectId studentId, ObjectId chatRoomId, Instant before, int limit) {

    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this room");

    if (limit < 1 || limit > 100) limit = 20;
    var page = PageRequest.of(0, limit + 1);

    List<ChatMessage> raw = (before == null)
      ? msgRepo.findByChatRoomIdOrderBySentAtDesc(chatRoomId, page)
      : msgRepo.findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, before, page);

    boolean hasMore = raw.size() > limit;
    if (hasMore) raw = raw.subList(0, limit);

    List<StudentChatMessageDTO> dto = raw.stream().map(this::toDto).toList();
    Instant nextBefore = dto.isEmpty() ? before : dto.get(dto.size() - 1).sentAt();

    return new StudentChatMessagesResponse(dto, nextBefore, hasMore);
  }

  /* 4 ─ FULL DUMP (ascending) */
  public List<StudentChatMessageDTO> getAllMessages(ObjectId studentId, ObjectId chatRoomId) {
    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this room");

    return msgRepo.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
      .map(this::toDto)
      .toList();
  }

  /* 5 ─ MARK AS SEEN (mentor & admin) */
  public void markMessagesAsSeen(ObjectId studentId, ObjectId chatRoomId, Instant until) {

    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this room");

    List<String> roles = List.of("MENTOR", "ADMIN");
    List<ChatMessage> unseen = msgRepo
      .findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
        chatRoomId, roles, until);

    unseen.forEach(m -> {
      m.getSeenStatus().setSeenByStudent(true);
      m.setSeenAt(Instant.now());
    });
    msgRepo.saveAll(unseen);
  }

  /* helper */
  private StudentChatMessageDTO toDto(ChatMessage m) {
    return new StudentChatMessageDTO(
      m.getId().toHexString(),
      m.getSenderId().toHexString(),
      m.getSenderRole(),
      m.getText(),
      m.getMediaUrls(),
      m.getSeenStatus(),
      m.getSeenAt(),
      m.getSentAt()
    );
  }
}
