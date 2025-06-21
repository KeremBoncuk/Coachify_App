package io.coachify.exception;

public class JwtAuthenticationException extends RuntimeException {
  public JwtAuthenticationException(String message) {
    super("JWT ERROR: " + message);
  }

  public JwtAuthenticationException(String message, Throwable cause) {
    super("JWT ERROR: " + message, cause);
  }
}
