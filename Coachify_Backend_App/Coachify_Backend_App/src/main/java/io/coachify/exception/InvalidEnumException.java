package io.coachify.exception;

public class InvalidEnumException extends RuntimeException {
  public InvalidEnumException(String field, String value) {
    super("Invalid enum value for " + field + ": " + value);
  }
}
