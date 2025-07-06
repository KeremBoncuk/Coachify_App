package io.coachify.config.websocket;

import io.coachify.entity.jwt.UserRole;
import io.coachify.security.CustomPrincipal;
import io.coachify.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Extracts the JWT during the HTTP → WebSocket upgrade and builds an
 * Authentication object so that @PreAuthorize works inside STOMP controllers.
 */
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtService jwtService;

  @Override
  public boolean beforeHandshake(ServerHttpRequest req,
                                 ServerHttpResponse res,
                                 WebSocketHandler handler,
                                 Map<String, Object> attributes) {

    String token = extractToken(req);

    if (token != null && jwtService.isTokenValid(token)) {
      ObjectId userId = jwtService.extractUserId(token);
      UserRole role   = jwtService.extractUserRole(token);

      // Use your CustomPrincipal here
      CustomPrincipal principal = new CustomPrincipal(userId, role);

      var auth = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        principal.getAuthorities() // Uses roles from your CustomPrincipal
      );

      // Attach authentication to the SecurityContext
      SecurityContextHolder.getContext().setAuthentication(auth);

      // Also optionally store it in the WebSocket session attributes
      attributes.put(StompHeaderAccessor.USER_HEADER, auth);
    }

    // Always allow the handshake itself; STOMP controllers will enforce security later.
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest req,
                             ServerHttpResponse res,
                             WebSocketHandler handler,
                             Exception ex) {
    // Nothing needed here
  }

  private String extractToken(ServerHttpRequest req) {
    // 1. Check standard HTTP Authorization header
    String header = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }

    // 2. Fallback: allow token via URL query parameter ?token=JWT
    if (req instanceof ServletServerHttpRequest sreq) {
      return sreq.getServletRequest().getParameter("token");
    }

    return null;
  }
}
