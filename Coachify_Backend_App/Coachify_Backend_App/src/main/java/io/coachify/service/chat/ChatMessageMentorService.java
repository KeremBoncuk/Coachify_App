package io.coachify.service.chat;

import io.coachify.dto.chat.mentor.*;
import io.coachify.entity.chat.*;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.repo.MentorRepository;
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
public class ChatMessageMentorService {

  private final ChatRoomRepository    roomRepo;
  private final ChatMessageRepository msgRepo;
  private final MentorRepository      mentorRepo;
  private final StudentRepository     studentRepo;   // ✅ added to fetch student names

  /* 1 ─ SEND */
  public void sendMessageByMentor(ObjectId mentorId, MentorSendMessageRequest req) {

    Mentor mentor = mentorRepo.findById(mentorId)
      .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

    ObjectId roomId = new ObjectId(req.chatRoomId());
    ChatRoom room   = roomRepo.findById(roomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

    if (!room.isActive())                     throw new IllegalStateException("Chat room inactive");
    if (!room.getMentorId().equals(mentorId)) throw new SecurityException("Mentor not in this room");

    boolean textEmpty = !StringUtils.hasText(req.text());
    boolean noMedia   = req.mediaUrls() == null || req.mediaUrls().isEmpty();
    if (textEmpty && noMedia)
      throw new IllegalArgumentException("Message must contain text or media");

    ChatMessage m = new ChatMessage();
    m.setChatRoomId(roomId);
    m.setSenderId(mentorId);
    m.setSenderRole("MENTOR");
    m.setText(req.text());
    m.setMediaUrls(noMedia ? List.of() : req.mediaUrls());
    m.setSentAt(Instant.now());
    m.setSeenStatus(new SeenStatus(false, true));         // mentor sees own

    msgRepo.save(m);
  }

  /* 2 ─ ACTIVE ROOMS (with student full name) */
  public List<MentorChatRoomDTO> getActiveChatRoomsForMentor(ObjectId mentorId) {
    return roomRepo.findByMentorIdAndIsActiveTrue(mentorId).stream()
      .map(r -> {
        String studentName = studentRepo.findById(r.getStudentId())
          .map(Student::getFullName)
          .orElse("(unknown)");
        return new MentorChatRoomDTO(
          r.getId().toHexString(),
          r.getStudentId().toHexString(),
          r.getMentorId().toHexString(),
          studentName,                        // ✅ inject full name here
          r.getCreatedAt(),
          r.isActive());
      })
      .toList();
  }

  /* 3 ─ PAGINATED (cursor exclusive) */
  public MentorChatMessagesResponse getMessages(
    ObjectId mentorId, ObjectId chatRoomId, Instant before, int limit) {

    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this room");

    if (limit < 1 || limit > 100) limit = 20;

    var page = PageRequest.of(0, limit + 1);   // ask one extra
    List<ChatMessage> raw = (before == null)
      ? msgRepo.findByChatRoomIdOrderBySentAtDesc(chatRoomId, page)
      : msgRepo.findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, before, page);

    boolean hasMore = raw.size() > limit;
    if (hasMore) raw = raw.subList(0, limit);

    List<MentorChatMessageDTO> dto = raw.stream().map(this::toDto).toList();
    Instant nextBefore = dto.isEmpty() ? before : dto.get(dto.size() - 1).sentAt();

    return new MentorChatMessagesResponse(dto, nextBefore, hasMore);
  }

  /* 4 ─ FULL DUMP (ascending) */
  public List<MentorChatMessageDTO> getAllMessages(ObjectId mentorId, ObjectId chatRoomId) {
    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this room");

    return msgRepo.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
      .map(this::toDto)
      .toList();
  }

  /* 5 ─ MARK AS SEEN */
  public void markMessagesAsSeen(ObjectId mentorId, ObjectId chatRoomId, Instant until) {

    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found / inactive"));
    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this room");

    List<String> roles = List.of("STUDENT", "ADMIN");
    List<ChatMessage> unseen = msgRepo
      .findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
        chatRoomId, roles, until);

    unseen.forEach(m -> {
      m.getSeenStatus().setSeenByMentor(true);
      m.setSeenAt(Instant.now());
    });
    msgRepo.saveAll(unseen);
  }

  /* helper */
  private MentorChatMessageDTO toDto(ChatMessage m) {
    return new MentorChatMessageDTO(
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
