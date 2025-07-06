package io.coachify.controller.admin;

import io.coachify.dto.admin.assign.AssignMentorRequest;
import io.coachify.service.admin.assign.MentorAssignmentOrchestratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * POST /admin/students/assign-mentor
 * Body: { "studentId": "<hex>", "mentorId": "<hex>" }
 */
@RestController
@RequestMapping("/admin/students")
@PreAuthorize("hasRole('ADMIN')")   // 🔐 restrict to admins
@RequiredArgsConstructor
public class MentorAssignmentController {

  private final MentorAssignmentOrchestratorService orchestratorService;

  @PostMapping("/assign-mentor")
  public ResponseEntity<Void> assignMentor(@RequestBody @Valid AssignMentorRequest req) {
    orchestratorService.assignMentorAndHandleChatRooms(req.getStudentId(), req.getMentorId());
    return ResponseEntity.noContent().build();   // 204 No Content
  }
}
