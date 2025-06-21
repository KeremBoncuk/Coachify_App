package io.coachify.service.auth;

import io.coachify.dto.auth.LoginRequest;
import io.coachify.entity.jwt.UserRole;
import io.coachify.exception.InvalidCredentialsException;
import io.coachify.repo.ActiveTokenRepository;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.security.JwtContextService;
import io.coachify.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final StudentRepository studentRepository;
  private final MentorRepository  mentorRepository;
  private final AdminRepository   adminRepository;

  private final PasswordEncoder       passwordEncoder;
  private final JwtService            jwtService;
  private final JwtContextService     ctx;
  private final ActiveTokenRepository activeTokenRepo;

  public String login(LoginRequest request) {
    final String fullName = request.fullName();
    final String rawPass  = request.password();
    final UserRole role   = request.role();

    return switch (role) {
      case STUDENT -> studentRepository.findByFullName(fullName)
        .filter(s -> passwordEncoder.matches(rawPass, s.getHashedPassword()))
        .map(s -> jwtService.generateToken(s.getId(), UserRole.STUDENT))
        .orElseThrow(() -> new InvalidCredentialsException("Invalid student credentials"));

      case MENTOR -> mentorRepository.findByFullName(fullName)
        .filter(m -> passwordEncoder.matches(rawPass, m.getHashedPassword()))
        .map(m -> jwtService.generateToken(m.getId(), UserRole.MENTOR))
        .orElseThrow(() -> new InvalidCredentialsException("Invalid mentor credentials"));

      case ADMIN -> adminRepository.findByFullName(fullName)
        .filter(a -> passwordEncoder.matches(rawPass, a.getHashedPassword()))
        .map(a -> jwtService.generateToken(a.getId(), UserRole.ADMIN))
        .orElseThrow(() -> new InvalidCredentialsException("Invalid admin credentials"));
    };
  }

  public void logout() {
    ObjectId userId  = ctx.getCurrentUserId();
    String   tokenId = ctx.getCurrentTokenId();

    activeTokenRepo.deleteByUserIdAndTokenId(userId, tokenId);
  }
}
