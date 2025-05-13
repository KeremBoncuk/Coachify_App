package io.coachify.security.service;

import io.coachify.entity.model.*;
import io.coachify.entity.repo.*;
import io.coachify.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthManager {

  private final JwtService jwtService;
  private final AdminRepository adminRepository;
  private final CoachRepository coachRepository;
  private final StudentRepository studentRepository;
  private final PasswordEncoder passwordEncoder;

  // ===== ADMIN =====
  public String registerAdmin(Admin admin) {
    admin.setId(new ObjectId());
    admin.setPassword(passwordEncoder.encode(admin.getPassword()));
    admin.setActive(true);
    admin.setCreatedAt(Instant.now());
    admin.setTokenVersion(0);
    admin.setUsername(admin.getEmail());
    admin.setRole(UserRole.ADMIN);
    adminRepository.save(admin);

    return jwtService.generateToken(admin.getId(), UserRole.ADMIN);
  }

  public String loginAdmin(String email, String rawPassword) {
    Admin admin = adminRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Admin not found"));

    if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
      throw new RuntimeException("Invalid password");
    }

    return jwtService.generateToken(admin.getId(), UserRole.ADMIN);
  }

  public void logoutAdmin(ObjectId adminId) {
    jwtService.invalidateToken(adminId);
  }

  // ===== COACH =====
  public String registerCoach(Coach coach) {
    coach.setId(new ObjectId());
    coach.setPassword(passwordEncoder.encode(coach.getPassword()));
    coach.setActive(true);
    coach.setCreatedAt(Instant.now());
    coach.setTokenVersion(0);
    coach.setUsername(coach.getEmail());
    coach.setRole(UserRole.COACH);
    coachRepository.save(coach);

    return jwtService.generateToken(coach.getId(), UserRole.COACH);
  }

  public String loginCoach(String email, String rawPassword) {
    Coach coach = coachRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Coach not found"));

    if (!passwordEncoder.matches(rawPassword, coach.getPassword())) {
      throw new RuntimeException("Invalid password");
    }

    return jwtService.generateToken(coach.getId(), UserRole.COACH);
  }

  public void logoutCoach(ObjectId coachId) {
    jwtService.invalidateToken(coachId);
  }

  // ===== STUDENT =====
  public String registerStudent(Student student) {
    student.setId(new ObjectId());
    student.setPassword(passwordEncoder.encode(student.getPassword()));
    student.setActive(true);
    student.setCreatedAt(Instant.now());
    student.setTokenVersion(0);
    student.setUsername(student.getEmail());
    student.setRole(UserRole.STUDENT);
    studentRepository.save(student);

    return jwtService.generateToken(student.getId(), UserRole.STUDENT);
  }

  public String loginStudent(String email, String rawPassword) {
    Student student = studentRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Student not found"));

    if (!passwordEncoder.matches(rawPassword, student.getPassword())) {
      throw new RuntimeException("Invalid password");
    }

    return jwtService.generateToken(student.getId(), UserRole.STUDENT);
  }

  public void logoutStudent(ObjectId studentId) {
    jwtService.invalidateToken(studentId);
  }
}
