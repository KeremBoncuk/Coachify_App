package io.coachify.service.chat;

import io.coachify.dto.chat.admin.AdminChatMessageResponse;
import io.coachify.dto.chat.admin.AdminSendMessageRequest;
import io.coachify.entity.chat.ChatMessage;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.chat.SeenStatus;
import io.coachify.entity.user.Admin;
import io.coachify.exception.BadRequestException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.chat.ChatMessageRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageAdminService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final AdminRepository adminRepository;

  public List<AdminChatMessageResponse> getAllMessages(ObjectId chatRoomId) {
    validateRoomExists(chatRoomId);
    return chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId)
      .stream()
      .map(this::toMessageResponse)
      .toList();
  }

  public List<AdminChatMessageResponse> getLimitedMessages(ObjectId chatRoomId, Instant before, int limit) {
    validateRoomExists(chatRoomId);
    if (limit <= 0 || limit > 100) {
      throw new BadRequestException("Limit must be between 1 and 100");
    }
    return chatMessageRepository.findLimitedMessages(chatRoomId, before, limit)
      .stream()
      .map(this::toMessageResponse)
      .toList();
  }

  public AdminChatMessageResponse sendMessage(ObjectId chatRoomId, AdminSendMessageRequest request, ObjectId adminId) {
    ChatRoom room = chatRoomRepository.findById(chatRoomId)
      .orElseThrow(() -> new NotFoundException("Chat room not found"));

    if (!room.isActive()) {
      throw new BadRequestException("Cannot send message to an inactive chat room");
    }

    Admin admin = adminRepository.findById(adminId)
      .orElseThrow(() -> new NotFoundException("Admin not found"));

    boolean hasText = request.text() != null && !request.text().isBlank();
    boolean hasMedia = request.mediaUrls() != null && !request.mediaUrls().isEmpty();

    if (!hasText && !hasMedia) {
      throw new BadRequestException("Message must have either text or media");
    }

    ChatMessage message = new ChatMessage();
    message.setChatRoomId(chatRoomId);
    message.setSenderId(adminId);
    message.setSenderRole("ADMIN");
    message.setText(request.text());
    message.setMediaUrls(request.mediaUrls());
    message.setSentAt(Instant.now());
    message.setSeenStatus(new SeenStatus(false, false));
    message.setSeenAt(null);

    return toMessageResponse(chatMessageRepository.save(message));
  }

  private void validateRoomExists(ObjectId chatRoomId) {
    if (!chatRoomRepository.existsById(chatRoomId)) {
      throw new NotFoundException("Chat room not found");
    }
  }

  private AdminChatMessageResponse toMessageResponse(ChatMessage msg) {
    return new AdminChatMessageResponse(
      msg.getId().toHexString(),
      msg.getChatRoomId().toHexString(),
      msg.getSenderId().toHexString(),
      msg.getSenderRole(),
      msg.getText(),
      msg.getMediaUrls(),
      msg.getSentAt(),
      msg.getSeenAt()
    );
  }
}
