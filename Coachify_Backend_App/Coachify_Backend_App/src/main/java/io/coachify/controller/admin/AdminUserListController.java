package io.coachify.controller.admin;

import io.coachify.dto.admin.AllUsersResponseDTO;
import io.coachify.dto.admin.StudentResponseDTO;
import io.coachify.dto.admin.MentorResponseDTO;
import io.coachify.service.admin.AdminUserListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  //Restrict all endpoints in this controller
public class AdminUserListController {

  private final AdminUserListingService adminUserListingService;

  @GetMapping("/students")
  public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
    List<StudentResponseDTO> students = adminUserListingService.getAllStudents();
    return ResponseEntity.ok(students);
  }

  @GetMapping("/mentors")
  public ResponseEntity<List<MentorResponseDTO>> getAllMentors() {
    List<MentorResponseDTO> mentors = adminUserListingService.getAllMentors();
    return ResponseEntity.ok(mentors);
  }

  @GetMapping("/all-users")
  public ResponseEntity<AllUsersResponseDTO> getAllUsers() {
    AllUsersResponseDTO response = adminUserListingService.getAllUsersGrouped();
    return ResponseEntity.ok(response);
  }
}
