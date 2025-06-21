package io.coachify.dto.auth;

import io.coachify.entity.jwt.UserRole;

public record LoginRequest(
  String fullName,
  String password,
  UserRole role
) {}
