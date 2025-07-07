package io.coachify.service.chat;

import io.coachify.dto.chat.ChatMessageResponseWs;
import io.coachify.dto.chat.WebSocketMessage;
import io.coachify.entity.chat.ChatMessage;
import io.coachify.entity.chat.ChatRoom;
import io.coachify.entity.chat.SeenStatus;
import io.coachify.exception.BadRequestException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.chat.ChatMessageRepository;
import io.coachify.repo.chat.ChatRoomRepository;
import io.coachify.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatMessageWsService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageWsService.class);

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public void processAndBroadcastMessage(WebSocketMessage webSocketMessage, Principal principal) {
        logger.info("Processing WebSocket message: {}", webSocketMessage);
        ObjectId chatRoomId = new ObjectId(webSocketMessage.getChatRoomId());

        CustomPrincipal customPrincipal = (CustomPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        ObjectId senderId = customPrincipal.getUserId();
        String senderRole = customPrincipal.getRole().name();

        logger.info("Extracted senderId: {}, senderRole: {}", senderId, senderRole);

        ChatRoom room = roomRepo.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        if (!room.isActive()) {
            throw new BadRequestException("Cannot send message to an inactive chat room");
        }

        // Authorization check: Ensure sender is a participant of the chat room
        if (senderRole.equals("STUDENT") && !room.getStudentId().equals(senderId)) {
            throw new BadRequestException("Student is not authorized to send messages in this chat room.");
        }
        if (senderRole.equals("MENTOR") && !room.getMentorId().equals(senderId)) {
            throw new BadRequestException("Mentor is not authorized to send messages in this chat room.");
        }
        // Admins are allowed to send messages to any chat room for monitoring/support purposes

        // Basic validation for message content
        boolean hasText = StringUtils.hasText(webSocketMessage.getText());
        boolean hasMedia = webSocketMessage.getMediaUrls() != null && !webSocketMessage.getMediaUrls().isEmpty();
        if (!hasText && !hasMedia) {
            throw new BadRequestException("Message must contain text or media");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setSenderId(senderId);
        chatMessage.setSenderRole(senderRole);
        chatMessage.setText(webSocketMessage.getText());
        chatMessage.setMediaUrls(webSocketMessage.getMediaUrls());
        chatMessage.setSentAt(Instant.now());

        // Set seen status based on sender role
        SeenStatus seenStatus = new SeenStatus(false, false);
        switch (senderRole) {
            case "STUDENT":
                seenStatus.setSeenByStudent(true);
                break;
            case "MENTOR":
                seenStatus.setSeenByMentor(true);
                break;
            case "ADMIN":
                seenStatus.setSeenByStudent(false); // Admin messages are not seen by student by default
                seenStatus.setSeenByMentor(false);  // Admin messages are not seen by mentor by default
                break;
        }
        chatMessage.setSeenStatus(seenStatus);

        logger.info("Saving chat message: {}", chatMessage);
        ChatMessage savedMessage = msgRepo.save(chatMessage);
        logger.info("Saved message with ID: {}", savedMessage.getId());

        ChatMessageResponseWs responseDto = toDto(savedMessage);

        // Broadcast the message to the specific chat room topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId.toHexString(), responseDto);
    }

    private ChatMessageResponseWs toDto(ChatMessage m) {
        return new ChatMessageResponseWs(
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
}
