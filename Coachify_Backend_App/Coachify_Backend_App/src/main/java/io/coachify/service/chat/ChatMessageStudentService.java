// src/main/java/io/coachify/service/chat/ChatMessageStudentService.java
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

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final StudentRepository studentRepository;

  /* ───────────────────────────── 1. SEND MESSAGE ───────────────────────────── */
  public void sendMessageByStudent(ObjectId studentId, StudentSendMessageRequest req) {

    Student student = studentRepository.findById(studentId)
      .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    ObjectId chatRoomId = new ObjectId(req.chatRoomId());
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

    if (!room.isActive())                  throw new IllegalStateException("Chat room inactive");
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this chat room");

    boolean emptyText  = !StringUtils.hasText(req.text());
    boolean noMedia    = req.mediaUrls() == null || req.mediaUrls().isEmpty();
    if (emptyText && noMedia)
      throw new IllegalArgumentException("Message must contain text or media");

    ChatMessage msg = new ChatMessage();
    msg.setChatRoomId(chatRoomId);
    msg.setSenderId(studentId);
    msg.setSenderRole("STUDENT");
    msg.setText(req.text());
    msg.setMediaUrls(noMedia ? List.of() : req.mediaUrls());
    msg.setSentAt(Instant.now());
    msg.setSeenStatus(new SeenStatus(true, false));   // student sees own message

    chatMessageRepository.save(msg);
  }

  /* ────────────────── 2. ALL ACTIVE ROOMS (IDs AS STRING) ─────────────────── */
  public List<StudentChatRoomDTO> getActiveChatRoomsForStudent(ObjectId studentId) {
    return chatRoomRepository.findByStudentIdAndIsActiveTrue(studentId).stream()
      .map(r -> new StudentChatRoomDTO(
        r.getId().toHexString(),
        r.getStudentId().toHexString(),
        r.getMentorId().toHexString(),
        r.getCreatedAt(),
        r.isActive()))
      .toList();
  }

  /* ─────────────── 3. PAGINATED MESSAGES (robust + correct) ──────────────── */
  public StudentChatMessagesResponse getMessages(
    ObjectId studentId,
    ObjectId chatRoomId,
    Instant before,
    int limit
  ) {
    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this chat room");

    if (limit < 1 || limit > 100) limit = 20;

    // over-fetch by 1 to detect "hasMore"
    var page = PageRequest.of(0, limit + 1);
    List<ChatMessage> fetched = chatMessageRepository
      .findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, before, page);

    boolean hasMore = fetched.size() > limit;
    List<ChatMessage> limited = hasMore ? fetched.subList(0, limit) : fetched;

    List<StudentChatMessageDTO> dtos = limited.stream().map(this::toDto).toList();
    Instant nextBefore = dtos.isEmpty() ? before : dtos.get(dtos.size() - 1).sentAt();

    return new StudentChatMessagesResponse(dtos, nextBefore, hasMore);
  }

  /* ─────────────── 4. ALL MESSAGES (ASC ORDER FOR CHAT VIEW) ─────────────── */
  public List<StudentChatMessageDTO> getAllMessages(ObjectId studentId, ObjectId chatRoomId) {
    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this chat room");

    return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
      .map(this::toDto)
      .toList();
  }

  /* ─────────── 5. MARK AS SEEN (ADMIN **and** MENTOR msgs) ──────────── */
  public void markMessagesAsSeen(ObjectId studentId, ObjectId chatRoomId, Instant until) {
    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));
    if (!room.getStudentId().equals(studentId))
      throw new SecurityException("Student not in this chat room");

    List<String> roles = List.of("MENTOR", "ADMIN");
    List<ChatMessage> unseen = chatMessageRepository
      .findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByStudentFalse(
        chatRoomId, roles, until);

    unseen.forEach(m -> {
      m.getSeenStatus().setSeenByStudent(true);
      m.setSeenAt(Instant.now());
    });
    chatMessageRepository.saveAll(unseen);
  }

  /* ─────────────────────────── helper → DTO ─────────────────────────── */
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
