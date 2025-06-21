package io.coachify.security;

import io.coachify.entity.jwt.UserRole;
import io.coachify.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Convenience wrapper around SecurityContextHolder + JwtService
 * so service classes don’t have to repeat casting/parsing code.
 */
@Service
@RequiredArgsConstructor
public class JwtContextService {

  private final JwtService jwtService;

  /** Returns the current user’s MongoDB ObjectId from the CustomPrincipal */
  public ObjectId getCurrentUserId() {
    return getPrincipal().getUserId();
  }

  /** Returns the current user’s role (STUDENT, MENTOR, ADMIN) */
  public UserRole getCurrentUserRole() {
    return getPrincipal().getRole();
  }

  /**
   * Returns the current token’s ID (jti).
   * This implementation re-reads the raw token from the Authorization header
   * and asks JwtService to parse it, so you do NOT need a custom
   * JwtAuthenticationToken or to store tokenId in Authentication details.
   */
  public String getCurrentTokenId() {
    HttpServletRequest request =
      ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes())
        .getRequest();

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new JwtAuthenticationException("Missing or bad Authorization header");
    }

    String token = authHeader.substring(7);
    return jwtService.extractTokenId(token);
  }

  /* ======== internal helper ======== */

  private CustomPrincipal getPrincipal() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof CustomPrincipal principal)) {
      throw new JwtAuthenticationException("No authenticated CustomPrincipal in context");
    }
    return principal;
  }
}
