package io.coachify.controller.admin;

import io.coachify.dto.admin.register.CreatedUserResponse;
import io.coachify.dto.admin.register.RegisterAdminRequest;
import io.coachify.dto.admin.register.RegisterMentorRequest;
import io.coachify.dto.admin.register.RegisterStudentRequest;
import io.coachify.service.admin.user.AdminUserCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 🔒 Restricts all endpoints in this class to ADMIN
public class AdminUserCreationController {

  private final AdminUserCreationService creationService;

  @PostMapping("/register-student")
  public ResponseEntity<CreatedUserResponse> registerStudent(@RequestBody @Valid RegisterStudentRequest request) {
    var id = creationService.registerStudent(request);
    return ResponseEntity.status(CREATED).body(new CreatedUserResponse(id.toHexString()));
  }

  @PostMapping("/register-mentor")
  public ResponseEntity<CreatedUserResponse> registerMentor(@RequestBody @Valid RegisterMentorRequest request) {
    var id = creationService.registerMentor(request);
    return ResponseEntity.status(CREATED).body(new CreatedUserResponse(id.toHexString()));
  }

  @PostMapping("/register-admin")
  public ResponseEntity<CreatedUserResponse> registerAdmin(@RequestBody @Valid RegisterAdminRequest request) {
    var id = creationService.registerAdmin(request);
    return ResponseEntity.status(CREATED).body(new CreatedUserResponse(id.toHexString()));
  }

  @GetMapping("/ping")
  public String ping() {
    return "Admin controller is alive!";
  }
}
