package io.coachify.exception;

public class DuplicateFullNameException extends RuntimeException {
  public DuplicateFullNameException(String fullName) {
    super("A user with full name '" + fullName + "' already exists.");
  }
}
