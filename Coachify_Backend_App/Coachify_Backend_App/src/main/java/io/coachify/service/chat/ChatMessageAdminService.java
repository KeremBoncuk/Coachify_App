package io.coachify.service.chat;

import io.coachify.dto.chat.admin.*;
import io.coachify.entity.chat.*;
import io.coachify.entity.user.Admin;
import io.coachify.exception.*;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.chat.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageAdminService {

  private final ChatRoomRepository    roomRepo;
  private final ChatMessageRepository msgRepo;
  private final AdminRepository       adminRepo;

  /* ───────────────────────────────────────────────────────────────
     Legacy “dump all messages” — kept because other services still
     depend on it (e.g. student / mentor view).
  ─────────────────────────────────────────────────────────────── */
  public List<AdminChatMessageResponse> getAllMessages(ObjectId roomId) {
    validateRoomExists(roomId);
    return msgRepo.findByChatRoomIdOrderBySentAtDesc(roomId)
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ────────────────── Paginated newest→oldest (exclusive cursor) ────────────────── */
  public AdminChatMessagePage getPaginatedMessages(
    ObjectId roomId, Instant before, Integer limit) {

    validateRoomExists(roomId);

    int size = (limit == null || limit <= 0 || limit > 100) ? 20 : limit;
    var pageable = PageRequest.of(0, size + 1);            // ask +1 for hasMore

    List<ChatMessage> raw = (before == null)
      ? msgRepo.findByChatRoomIdOrderBySentAtDesc(roomId, pageable)
      : msgRepo.findByChatRoomIdAndSentAtBeforeOrderBySentAtDesc(roomId, before, pageable);

    boolean hasMore = raw.size() > size;
    if (hasMore) raw = raw.subList(0, size);

    List<AdminChatMessageResponse> dto =
      raw.stream().map(this::toDto).toList();

    Instant nextBefore = hasMore
      ? raw.get(raw.size() - 1).getSentAt()
      : null;

    return new AdminChatMessagePage(dto, hasMore, nextBefore);
  }

  /* ───────────────────────── sendMessage (unchanged) ───────────────────────── */
  public AdminChatMessageResponse sendMessage(ObjectId roomId,
                                              AdminSendMessageRequest req,
                                              ObjectId adminId) {

    ChatRoom room = roomRepo.findById(roomId)
      .orElseThrow(() -> new NotFoundException("Chat room not found"));

    if (!room.isActive())
      throw new BadRequestException("Cannot send message to an inactive chat room");

    adminRepo.findById(adminId)
      .orElseThrow(() -> new NotFoundException("Admin not found"));

    boolean hasText  = req.text() != null  && !req.text().isBlank();
    boolean hasMedia = req.mediaUrls() != null && !req.mediaUrls().isEmpty();
    if (!hasText && !hasMedia)
      throw new BadRequestException("Message must contain text or media");

    ChatMessage m = new ChatMessage();
    m.setChatRoomId(roomId);
    m.setSenderId(adminId);
    m.setSenderRole("ADMIN");
    m.setText(req.text());
    m.setMediaUrls(req.mediaUrls());
    m.setSentAt(Instant.now());
    m.setSeenStatus(new SeenStatus(false, false));

    return toDto(msgRepo.save(m));
  }

  /* ───────────────────────── helpers ───────────────────────── */
  private void validateRoomExists(ObjectId id) {
    if (!roomRepo.existsById(id))
      throw new NotFoundException("Chat room not found");
  }

  private AdminChatMessageResponse toDto(ChatMessage m) {
    return new AdminChatMessageResponse(
      m.getId().toHexString(),
      m.getChatRoomId().toHexString(),
      m.getSenderId().toHexString(),
      m.getSenderRole(),
      m.getText(),
      m.getMediaUrls(),
      m.getSentAt(),
      m.getSeenAt()
    );
  }
}
