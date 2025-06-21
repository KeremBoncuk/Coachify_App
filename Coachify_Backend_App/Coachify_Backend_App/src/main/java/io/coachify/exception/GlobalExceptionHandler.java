package io.coachify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
      "timestamp", Instant.now(),
      "error", "Unauthorized",
      "message", ex.getMessage(),
      "status", 401
    ));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
      "timestamp", Instant.now(),
      "error", "Bad Request",
      "message", ex.getMessage(),
      "status", 400
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnknown(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
      "timestamp", Instant.now(),
      "error", "Internal Server Error",
      "message", ex.getMessage(),
      "status", 500
    ));
  }

  @ExceptionHandler(JwtAuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleJwtError(JwtAuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
      "timestamp", Instant.now(),
      "error", "Unauthorized",
      "message", ex.getMessage(),
      "status", 401
    ));
  }
}



