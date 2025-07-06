package io.coachify.service.admin.assign;

import io.coachify.exception.ConflictException;
import io.coachify.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorAssignmentOrchestratorService {

  private final MentorAssignmentService mentorAssignmentService;
  private final ChatRoomCreationService chatRoomCreationService;

  /**
   * Assign or unassign mentor & update chat-rooms in a single transaction.
   * If mentorIdHex is blank → unassign mentor and deactivate chatrooms.
   */
  @Transactional
  public void assignMentorAndHandleChatRooms(String studentIdHex, String mentorIdHex) {
    ObjectId studentId = new ObjectId(studentIdHex);

    if (mentorIdHex == null || mentorIdHex.isBlank()) {
      // Unassign flow
      ObjectId previousMentorId = mentorAssignmentService.unassignMentor(studentId);
      chatRoomCreationService.handleUnassignment(studentId, previousMentorId);
      return;
    }

    ObjectId newMentorId = new ObjectId(mentorIdHex);
    ObjectId previousMentorId = mentorAssignmentService.assignMentor(studentId, newMentorId);
    chatRoomCreationService.handleChatRoomsForMentorChange(studentId, previousMentorId, newMentorId);
  }
}
