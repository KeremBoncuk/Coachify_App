// src/main/java/io/coachify/service/chat/ChatMessageMentorService.java
package io.coachify.service.chat;

import io.coachify.dto.chat.mentor.*;
import io.coachify.entity.chat.*;
import io.coachify.entity.user.Mentor;
import io.coachify.repo.MentorRepository;
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

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MentorRepository mentorRepository;

  /* ───────────────────────────── 1. SEND MESSAGE ───────────────────────────── */
  public void sendMessageByMentor(ObjectId mentorId, MentorSendMessageRequest req) {

    Mentor mentor = mentorRepository.findById(mentorId)
      .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

    ObjectId chatRoomId = new ObjectId(req.chatRoomId());
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

    if (!room.isActive())                      throw new IllegalStateException("Chat room inactive");
    if (!room.getMentorId().equals(mentorId))  throw new SecurityException("Mentor not in this chat room");

    boolean emptyText = !StringUtils.hasText(req.text());
    boolean noMedia   = req.mediaUrls() == null || req.mediaUrls().isEmpty();
    if (emptyText && noMedia)
      throw new IllegalArgumentException("Message must contain text or media");

    ChatMessage msg = new ChatMessage();
    msg.setChatRoomId(chatRoomId);
    msg.setSenderId(mentorId);
    msg.setSenderRole("MENTOR");
    msg.setText(req.text());
    msg.setMediaUrls(noMedia ? List.of() : req.mediaUrls());
    msg.setSentAt(Instant.now());
    msg.setSeenStatus(new SeenStatus(false, true));   // mentor sees own message

    chatMessageRepository.save(msg);
  }

  /* ─────────────────── 2. ACTIVE ROOMS (IDs AS STRING) ─────────────────────── */
  public List<MentorChatRoomDTO> getActiveChatRoomsForMentor(ObjectId mentorId) {
    return chatRoomRepository.findByMentorIdAndIsActiveTrue(mentorId).stream()
      .map(r -> new MentorChatRoomDTO(
        r.getId().toHexString(),
        r.getStudentId().toHexString(),
        r.getMentorId().toHexString(),
        r.getCreatedAt(),
        r.isActive()))
      .toList();
  }

  /* ──────────────── 3. PAGINATED MESSAGES (robust + correct) ──────────────── */
  public MentorChatMessagesResponse getMessages(
    ObjectId mentorId,
    ObjectId chatRoomId,
    Instant before,
    int limit
  ) {
    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));
    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this chat room");

    if (limit < 1 || limit > 100) limit = 20;

    var page = PageRequest.of(0, limit + 1);                    // over-fetch by 1
    List<ChatMessage> fetched = chatMessageRepository
      .findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(chatRoomId, before, page);

    boolean hasMore = fetched.size() > limit;
    List<ChatMessage> limited = hasMore ? fetched.subList(0, limit) : fetched;

    List<MentorChatMessageDTO> dtos = limited.stream().map(this::toDto).toList();
    Instant nextBefore = dtos.isEmpty() ? before : dtos.get(dtos.size() - 1).sentAt();

    return new MentorChatMessagesResponse(dtos, nextBefore, hasMore);
  }

  /* ─────────────────────── 4. ALL MESSAGES (ASC ORDER) ─────────────────────── */
  public List<MentorChatMessageDTO> getAllMessages(ObjectId mentorId, ObjectId chatRoomId) {
    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));
    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this chat room");

    return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
      .map(this::toDto)
      .toList();
  }

  /* ──────────────── 5. MARK STUDENT MESSAGES AS SEEN ───────────────────────── */
  public void markMessagesAsSeen(ObjectId mentorId, ObjectId chatRoomId, Instant until) {

    ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
      .orElseThrow(() -> new IllegalArgumentException("Chat room not found or inactive"));

    if (!room.getMentorId().equals(mentorId))
      throw new SecurityException("Mentor not in this chat room");

    /* fetch both STUDENT and ADMIN messages that the mentor hasn’t seen yet */
    List<String> roles = List.of("STUDENT", "ADMIN");
    List<ChatMessage> unseen = chatMessageRepository
      .findByChatRoomIdAndSenderRoleInAndSentAtLessThanEqualAndSeenStatus_SeenByMentorFalse(
        chatRoomId, roles, until);

    unseen.forEach(m -> {
      m.getSeenStatus().setSeenByMentor(true);
      m.setSeenAt(Instant.now());
    });
    chatMessageRepository.saveAll(unseen);
  }


  /* ───────────────────────────── helper → DTO ─────────────────────────────── */
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
