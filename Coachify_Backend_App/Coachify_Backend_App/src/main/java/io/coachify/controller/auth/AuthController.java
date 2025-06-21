package io.coachify.controller.auth;

import io.coachify.dto.auth.LoginRequest;
import io.coachify.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * Login endpoint
   * Example Request Body:
   * {
   *   "fullName": "Kemal Inan",
   *   "password": "123456",
   *   "role": "MENTOR"
   * }
   */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    String jwt = authService.login(request);
    return ResponseEntity.ok(jwt);
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
}
