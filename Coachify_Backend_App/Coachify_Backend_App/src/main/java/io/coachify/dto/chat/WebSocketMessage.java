package io.coachify.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String chatRoomId;
    private String text;
    private List<String> mediaUrls;
    // No sender details needed here, as they are handled by the WebSocket session
}
