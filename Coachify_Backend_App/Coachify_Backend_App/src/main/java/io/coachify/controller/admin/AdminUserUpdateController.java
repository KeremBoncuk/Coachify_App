package io.coachify.controller.admin;

import io.coachify.dto.admin.update.UpdateAdminRequest;
import io.coachify.dto.admin.update.UpdateMentorRequest;
import io.coachify.dto.admin.update.UpdateStudentRequest;
import io.coachify.service.admin.user.AdminUserUpdateService;
import io.coachify.service.admin.assign.StudentDeactivationHandlerService;
import io.coachify.service.admin.assign.MentorDeactivationHandlerService;   // ✅ NEW import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 🔐 restrict to admin users only
public class AdminUserUpdateController {

  private final AdminUserUpdateService updateService;

  private final StudentDeactivationHandlerService studentDeactivationHandlerService;
  private final MentorDeactivationHandlerService  mentorDeactivationHandlerService;  // ✅ Inject handler

  /* ─────────── Students ─────────── */

  @PutMapping("/update-student")
  public ResponseEntity<Void> updateStudent(@RequestBody @Valid UpdateStudentRequest request) {
    updateService.updateStudent(request);

    // ✅ Automatically unassign the student if they became inactive
    studentDeactivationHandlerService.forceUnassignStudent(request.getId());

    return ResponseEntity.ok().build();
  }

  /* ─────────── Mentors ─────────── */

  @PutMapping("/update-mentor")
  public ResponseEntity<Void> updateMentor(@RequestBody @Valid UpdateMentorRequest request) {
    updateService.updateMentor(request);

    // ✅ If mentor is now inactive → unassign ALL their students
    mentorDeactivationHandlerService.unassignStudentsIfMentorInactive(request.getId());

    return ResponseEntity.ok().build();
  }

  /* ─────────── Admins ─────────── */

  @PutMapping("/update-admin")
  public ResponseEntity<Void> updateAdmin(@RequestBody @Valid UpdateAdminRequest request) {
    updateService.updateAdmin(request);
    return ResponseEntity.ok().build();
  }
}
