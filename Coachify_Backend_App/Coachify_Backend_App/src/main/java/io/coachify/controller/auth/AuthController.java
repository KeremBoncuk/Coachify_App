package io.coachify.controller.auth;

import io.coachify.dto.auth.LoginRequest;
import io.coachify.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * Login endpoint
   * Example Request Body:
   * {
   *   "fullName": "Dev Admin",
   *   "password": "admin123",
   *   "role": "ADMIN"
   * }
   */
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
    String jwt = authService.login(request);
    return ResponseEntity.ok(Map.of("token", jwt));  // Wrapped in JSON
  }

  /**
   * Logout endpoint
   * Requires Authorization header: Bearer <token>
   */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    authService.logout();
    return ResponseEntity.noContent().build();
  }

  /**
   * JWT validation endpoint.
   * Used by frontend to verify if the token is still valid & active in DB.
   */
  @GetMapping("/validate-token")
  public ResponseEntity<Void> validateToken() {
    // If this method is called, token is valid — JwtAuthenticationFilter already ran.
    return ResponseEntity.ok().build();
  }
}
