package io.coachify.dto.chat;

import io.coachify.entity.chat.SeenStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseWs {
    private String id;
    private String chatRoomId;
    private String senderId;
    private String senderRole;
    private String text;
    private List<String> mediaUrls;
    private SeenStatus seenStatus;
    private Instant seenAt;
    private Instant sentAt;
}
