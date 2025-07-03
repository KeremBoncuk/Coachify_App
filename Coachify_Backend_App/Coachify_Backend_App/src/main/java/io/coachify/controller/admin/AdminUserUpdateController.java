package io.coachify.controller.admin;

import io.coachify.dto.admin.UpdateAdminRequest;
import io.coachify.dto.admin.UpdateMentorRequest;
import io.coachify.dto.admin.UpdateStudentRequest;
import io.coachify.service.admin.AdminUserUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 🔐 restrict to admin users only
public class AdminUserUpdateController {

  private final AdminUserUpdateService updateService;

  @PutMapping("/update-student")
  public ResponseEntity<Void> updateStudent(@RequestBody UpdateStudentRequest request) {
    updateService.updateStudent(request);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/update-mentor")
  public ResponseEntity<Void> updateMentor(@RequestBody UpdateMentorRequest request) {
    updateService.updateMentor(request);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/update-admin")
  public ResponseEntity<Void> updateAdmin(@RequestBody UpdateAdminRequest request) {
    updateService.updateAdmin(request);
    return ResponseEntity.ok().build();
  }
}
