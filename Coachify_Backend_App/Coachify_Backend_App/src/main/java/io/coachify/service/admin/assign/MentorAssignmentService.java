package io.coachify.service.admin.assign;

import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.exception.ConflictException;
import io.coachify.exception.NotFoundException;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorAssignmentService {

  private final StudentRepository studentRepo;
  private final MentorRepository  mentorRepo;

  /* ───────────── ASSIGN ───────────── */

  public ObjectId assignMentor(ObjectId studentId, ObjectId newMentorId) {
    Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

    if (!student.isActive()) {
      throw new ConflictException("Cannot assign a mentor to an inactive student.");
    }

    Mentor mentor = mentorRepo.findById(newMentorId)
            .orElseThrow(() -> new NotFoundException("Mentor not found"));

    if (!mentor.isActive()) {
      throw new ConflictException("Cannot assign an inactive mentor.");
    }

    ObjectId currentMentorId = student.getAssignedMentor();

    if (newMentorId.equals(currentMentorId)) {
      throw new ConflictException("Student is already assigned to this mentor.");
    }

    student.setAssignedMentor(newMentorId);
    studentRepo.save(student);
    return currentMentorId;
  }

  /* ───────────── UNASSIGN ──────────── */

  public ObjectId unassignMentor(ObjectId studentId) {
    Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

    ObjectId currentMentorId = student.getAssignedMentor();
    if (currentMentorId == null) return null;

    student.setAssignedMentor(null);
    studentRepo.save(student);
    return currentMentorId;
  }
}
