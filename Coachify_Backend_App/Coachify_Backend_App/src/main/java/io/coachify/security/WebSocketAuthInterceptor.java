package io.coachify.security;

import io.coachify.entity.jwt.UserRole;
import io.coachify.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        logger.info("Intercepting WebSocket message: {}", accessor.getCommand());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            logger.info("Attempting to authenticate WebSocket connection with token.");
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                try {
                    if (jwtService.isTokenValid(token)) {
                        ObjectId userId = jwtService.extractUserId(token);
                        UserRole userRole = jwtService.extractUserRole(token);

                        CustomPrincipal principal = new CustomPrincipal(userId, userRole);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);
                        logger.info("WebSocket connection authenticated for user: {}", userId);
                    }
                } catch (JwtAuthenticationException e) {
                    logger.error("WebSocket authentication failed: {}", e.getMessage());
                    // Optionally, throw an exception to prevent connection
                    // throw new MessageDeliveryException("Unauthorized");
                }
            }
        } else if (accessor.getCommand() != null) {
            logger.info("Setting authenticated user for command: {}", accessor.getCommand());
            // For other STOMP commands (SEND, SUBSCRIBE, UNSUBSCRIBE, etc.),
            // ensure the SecurityContextHolder is populated if a user is already authenticated.
            // This is crucial for @AuthenticationPrincipal to work in @MessageMapping methods.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                accessor.setUser(authentication);
            }
        }

        return message;
    }
}
