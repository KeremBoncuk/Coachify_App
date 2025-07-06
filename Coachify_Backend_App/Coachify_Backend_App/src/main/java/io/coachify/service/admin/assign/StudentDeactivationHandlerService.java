package io.coachify.service.admin.assign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles automatic unassignment of mentor and deactivation of chatrooms for a student.
 * This is a simple delegator that always unassigns the student.
 */
@Service
@RequiredArgsConstructor
public class StudentDeactivationHandlerService {

  private final MentorAssignmentOrchestratorService mentorAssignmentOrchestratorService;

  /**
   * Unassigns the student from their mentor and deactivates any active chatrooms.
   * This operation is always performed.
   *
   * @param studentIdHex The student ID (hex string)
   */
  public void forceUnassignStudent(String studentIdHex) {
    mentorAssignmentOrchestratorService.assignMentorAndHandleChatRooms(studentIdHex, "");
  }
}
