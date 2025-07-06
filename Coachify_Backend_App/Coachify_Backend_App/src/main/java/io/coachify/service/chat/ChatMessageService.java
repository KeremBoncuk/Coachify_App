package io.coachify.service.chat;

import io.coachify.dto.chat.ChatMessagePayload;
import io.coachify.dto.chat.ChatMessageResponse;
import io.coachify.entity.chat.*;
import io.coachify.entity.jwt.UserRole;
import io.coachify.exception.BadRequestException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.chat.ChatMessageRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import io.coachify.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private static final int MAX_TEXT_LEN    = 2_000;
  private static final int MAX_MEDIA_COUNT = 10;
  private static final int BULK_SEEN_LIMIT = 10_000;   // safety cap

  private final ChatRoomRepository   roomRepo;
  private final ChatMessageRepository msgRepo;
  private final ChatMessagePublisher  publisher;
  private final ChatSeenPublisher     seenPublisher;

  /* ────────── SEND + BROADCAST ────────── */
  public void saveAndBroadcast(CustomPrincipal principal, ChatMessagePayload in) {

    ObjectId roomId = new ObjectId(in.getChatRoomId());
    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(roomId)
      .orElseThrow(() -> new NotFoundException("Chat room not found / inactive"));

    ObjectId senderId = principal.getUserId();
    UserRole role     = principal.getRole();

    /* participant validation */
    switch (role) {
      case STUDENT -> {
        if (!room.getStudentId().equals(senderId))
          throw new BadRequestException("Student not participant of this room");
      }
      case MENTOR -> {
        if (!room.getMentorId().equals(senderId))
          throw new BadRequestException("Mentor not participant of this room");
      }
      case ADMIN -> { /* always allowed */ }
      default -> throw new BadRequestException("Unsupported role");
    }

    /* content limits */
    boolean hasText  = StringUtils.hasText(in.getText());
    boolean hasMedia = in.getMediaUrls() != null && !in.getMediaUrls().isEmpty();

    if (!hasText && !hasMedia)
      throw new BadRequestException("Message must contain text or media");

    if (hasText && in.getText().length() > MAX_TEXT_LEN)
      throw new BadRequestException("Text exceeds " + MAX_TEXT_LEN + " chars");

    if (hasMedia && in.getMediaUrls().size() > MAX_MEDIA_COUNT)
      throw new BadRequestException("Too many media URLs (max " + MAX_MEDIA_COUNT + ")");

    /* build & save */
    ChatMessage m = new ChatMessage();
    m.setChatRoomId(roomId);
    m.setSenderId(senderId);
    m.setSenderRole(role.name());
    m.setText(hasText ? in.getText() : null);
    m.setMediaUrls(hasMedia ? in.getMediaUrls() : List.of());
    m.setSentAt(Instant.now());
    m.setSeenStatus(initialSeen(role));

    ChatMessage saved = msgRepo.save(m);
    publisher.publish(saved);          // 🔔 deliver message in real-time
  }

  /* ────────── MARK AS SEEN ────────── */
  public void markAsSeen(CustomPrincipal caller, String roomHex, Instant until) {

    ObjectId roomId = new ObjectId(roomHex);
    ChatRoom room = roomRepo.findByIdAndIsActiveTrue(roomId)
      .orElseThrow(() -> new NotFoundException("Room not found / inactive"));

    boolean flipStudentFlag = false, flipMentorFlag = false;

    switch (caller.getRole()) {
      case STUDENT -> {
        if (!room.getStudentId().equals(caller.getUserId()))
          throw new BadRequestException("Access denied");
        flipStudentFlag = true;
      }
      case MENTOR -> {
        if (!room.getMentorId().equals(caller.getUserId()))
          throw new BadRequestException("Access denied");
        flipMentorFlag = true;
      }
      default -> { return; } // admins don’t send seen markers
    }

    /* pull a bounded page of candidate messages */
    List<ChatMessage> page = msgRepo
      .findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(
        roomId, until, PageRequest.of(0, BULK_SEEN_LIMIT));

    /* collect only those that actually change */
    List<ChatMessage> toSave = new ArrayList<>();

    for (ChatMessage m : page) {
      boolean updated = false;
      SeenStatus s = m.getSeenStatus();

      if (flipStudentFlag && !s.isSeenByStudent()) {
        s.setSeenByStudent(true);
        updated = true;
      }
      if (flipMentorFlag && !s.isSeenByMentor()) {
        s.setSeenByMentor(true);
        updated = true;
      }
      if (updated) {
        m.setSeenAt(Instant.now());
        toSave.add(m);
      }
    }

    if (!toSave.isEmpty()) {
      msgRepo.saveAll(toSave);         // bulk write only changed docs
    }

    /* lightweight event so UI of the other side updates instantly */
    seenPublisher.publish(roomHex, caller.getRole().name(), until);
  }

  /* ────────── HISTORY (membership-aware) ────────── */
  public ChatHistoryPage getHistory(CustomPrincipal caller,
                                    ObjectId roomId,
                                    Instant before,
                                    int limit) {

    ChatRoom room = roomRepo.findById(roomId)
      .orElseThrow(() -> new NotFoundException("Room not found"));

    boolean illegal =
      (caller.getRole() == UserRole.STUDENT && !room.getStudentId().equals(caller.getUserId())) ||
        (caller.getRole() == UserRole.MENTOR  && !room.getMentorId().equals(caller.getUserId()));
    if (illegal)
      throw new BadRequestException("Access denied");

    if (limit < 1 || limit > 100) limit = 20;

    var pageReq = PageRequest.of(0, limit + 1); // fetch one extra to know hasMore

    List<ChatMessage> list = (before == null)
      ? msgRepo.findByChatRoomIdOrderBySentAtDesc(roomId, pageReq)
      : msgRepo.findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(roomId, before, pageReq);

    boolean hasMore = list.size() > limit;
    if (hasMore) list = list.subList(0, limit);

    Instant nextBefore = list.isEmpty() ? before
      : list.get(list.size() - 1).getSentAt();

    List<ChatMessageResponse> dto = list.stream().map(this::toDto).toList();
    return new ChatHistoryPage(dto, nextBefore, hasMore);
  }

  /* ────────── Helpers ────────── */
  private SeenStatus initialSeen(UserRole role) {
    return switch (role) {
      case STUDENT -> new SeenStatus(true,  false);
      case MENTOR  -> new SeenStatus(false, true );
      case ADMIN   -> new SeenStatus(false, false);
    };
  }

  private ChatMessageResponse toDto(ChatMessage m) {
    return new ChatMessageResponse(
      m.getId().toHexString(),
      m.getChatRoomId().toHexString(),
      m.getSenderId().toHexString(),
      m.getSenderRole(),
      m.getText(),
      m.getMediaUrls(),
      m.getSeenStatus(),
      m.getSeenAt(),
      m.getSentAt()
    );
  }

  /* ────────── Projection record ────────── */
  public record ChatHistoryPage(List<ChatMessageResponse> messages,
                                Instant nextBefore,
                                boolean hasMore) { }
}
