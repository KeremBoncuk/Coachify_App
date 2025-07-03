package io.coachify.service.admin;

import io.coachify.dto.admin.*;
import io.coachify.entity.user.Admin;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserListingService {

  private final StudentRepository studentRepository;
  private final MentorRepository mentorRepository;
  private final AdminRepository adminRepository;

  public List<StudentResponseDTO> getAllStudents() {
    return studentRepository.findAll().stream()
      .map(this::toStudentDTO)
      .toList();
  }

  public List<MentorResponseDTO> getAllMentors() {
    return mentorRepository.findAll().stream()
      .map(this::toMentorDTO)
      .toList();
  }

  public List<AdminResponseDTO> getAllAdmins() {
    return adminRepository.findAll().stream()
      .map(this::toAdminDTO)
      .toList();
  }

  public AllUsersResponseDTO getAllUsersGrouped() {
    return new AllUsersResponseDTO(
      getAllStudents(),
      getAllMentors(),
      getAllAdmins()
    );
  }

  private StudentResponseDTO toStudentDTO(Student s) {
    return new StudentResponseDTO(
      s.getId().toHexString(),
      s.getFullName(),
      s.getEmail(),
      s.getPhoneNumber(),
      s.getAssignedMentor() != null ? s.getAssignedMentor().toHexString() : null,
      s.getPurchaseDate(),
      s.getSubscriptionStartDate(),
      s.getNextPaymentDate(),
      s.getPaymentStatus(),
      s.getNotes(),
      s.getMentorChangeHistory(),
      s.isActive(),
      s.getAbandonmentDate()
    );
  }

  private MentorResponseDTO toMentorDTO(Mentor m) {
    List<String> studentIds = m.getAssignedStudents() != null
      ? m.getAssignedStudents().stream().map(ObjectId::toHexString).toList()
      : List.of();

    return new MentorResponseDTO(
      m.getId().toHexString(),
      m.getFullName(),
      m.getEmail(),
      m.getPhoneNumber(),
      m.getSchool(),
      m.getDepartment(),
      m.getPlacement(),
      m.getArea(),
      m.getBirthDate(),
      m.getIban(),
      m.getNotes(),
      studentIds,
      m.isActive(),
      m.getAbandonmentDate()
    );
  }

  private AdminResponseDTO toAdminDTO(Admin a) {
    return new AdminResponseDTO(
      a.getId().toHexString(),
      a.getFullName(),
      a.getEmail(),
      a.getPhoneNumber()
    );
  }
}
