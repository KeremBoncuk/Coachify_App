package io.coachify.service.admin.assign;

import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * When a mentor becomes inactive, this service:
 *   1. Finds every student assigned to that mentor.
 *   2. Unassigns each student (and deactivates chat-rooms) via the orchestrator.
 */
@Service
@RequiredArgsConstructor
public class MentorDeactivationHandlerService {

  private final MentorRepository mentorRepository;
  private final StudentRepository studentRepository;
  private final MentorAssignmentOrchestratorService orchestratorService;

  /**
   * Call after the mentor entity has been updated & saved.
   * If the mentor is now inactive, unassign all their students.
   *
   * @param mentorIdHex mentor id as hex string
   */
  @Transactional
  public void unassignStudentsIfMentorInactive(String mentorIdHex) {
    ObjectId mentorId = new ObjectId(mentorIdHex);

    Mentor mentor = mentorRepository.findById(mentorId)
      .orElseThrow(() -> new NotFoundException("Mentor not found"));

    if (mentor.isActive()) {
      return;   // Still active → nothing to do
    }

    // 🔍 All students whose assignedMentor == this mentor
    List<Student> students = studentRepository.findByAssignedMentor(mentorId);

    for (Student s : students) {
      // "" (blank) means unassign in the orchestrator
      orchestratorService.assignMentorAndHandleChatRooms(s.getId().toHexString(), "");
    }
  }
}
