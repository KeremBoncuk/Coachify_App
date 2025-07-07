package io.coachify.controller.mentor;

import io.coachify.dto.mentor.MentorStudentResponseDTO;
import io.coachify.security.JwtContextService;
import io.coachify.service.mentor.MentorStudentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mentor")
@RequiredArgsConstructor
public class MentorStudentController {

  private final MentorStudentService mentorStudentService;
  private final JwtContextService jwtContextService;

  @PreAuthorize("hasRole('MENTOR')")
  @GetMapping("/students")
  public List<MentorStudentResponseDTO> getAssignedStudents() {
    ObjectId mentorId = jwtContextService.getCurrentUserId();

    // ✅ Debug log: mentorId from JWT
    System.out.println("🔍 MentorStudentController → Mentor ID from JWT: " + mentorId);

    List<MentorStudentResponseDTO> students = mentorStudentService.getAssignedStudents(mentorId);

    // ✅ Debug log: number of students found
    System.out.println("🔍 MentorStudentController → Number of assigned students: " + students.size());

    return students;
  }
}
