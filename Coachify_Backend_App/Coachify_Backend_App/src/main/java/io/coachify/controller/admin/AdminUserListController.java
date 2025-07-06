package io.coachify.controller.admin;

import io.coachify.dto.admin.list.AllUsersResponseDTO;
import io.coachify.dto.admin.list.StudentResponseDTO;
import io.coachify.dto.admin.list.MentorResponseDTO;
import io.coachify.service.admin.user.AdminUserListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")          // 🔐 restrict controller
public class AdminUserListController {

  private final AdminUserListingService listingService;

  @GetMapping("/students")
  public ResponseEntity<List<StudentResponseDTO>> getStudents(
    @RequestParam(value = "onlyActive", required = false) Boolean onlyActive) {

    return ResponseEntity.ok(listingService.getAllStudents(onlyActive));
  }

  @GetMapping("/mentors")
  public ResponseEntity<List<MentorResponseDTO>> getMentors(
    @RequestParam(value = "onlyActive", required = false) Boolean onlyActive) {

    return ResponseEntity.ok(listingService.getAllMentors(onlyActive));
  }

  @GetMapping("/all-users")
  public ResponseEntity<AllUsersResponseDTO> getAllUsers() {
    return ResponseEntity.ok(listingService.getAllUsersGrouped());
  }
}
