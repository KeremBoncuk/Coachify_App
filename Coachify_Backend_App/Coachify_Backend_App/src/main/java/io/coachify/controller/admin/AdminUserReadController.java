package io.coachify.controller.admin;

import io.coachify.dto.admin.list.StudentResponseDTO;
import io.coachify.dto.admin.list.MentorResponseDTO;
import io.coachify.dto.admin.list.AdminResponseDTO;
import io.coachify.service.admin.user.AdminUserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔒 Restricts all endpoints to admins
public class AdminUserReadController {

  private final AdminUserReadService readService;

  @GetMapping("/get-student")
  public StudentResponseDTO getStudentById(@RequestParam("studentId") String studentId) {
    return readService.getStudentById(studentId);
  }

  @GetMapping("/get-mentor")
  public MentorResponseDTO getMentorById(@RequestParam("mentorId") String mentorId) {
    return readService.getMentorById(mentorId);
  }

  @GetMapping("/get-admin")
  public AdminResponseDTO getAdminById(@RequestParam("adminId") String adminId) {
    return readService.getAdminById(adminId);
  }
}
