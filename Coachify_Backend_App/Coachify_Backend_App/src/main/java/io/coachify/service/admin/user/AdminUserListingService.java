package io.coachify.service.admin.user;

import io.coachify.dto.admin.list.*;
import io.coachify.entity.user.*;
import io.coachify.repo.*;
import io.coachify.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserListingService {

  private final StudentRepository studentRepo;
  private final MentorRepository  mentorRepo;
  private final AdminRepository   adminRepo;
  private final EncryptionUtil    encryption;   // AES-256 util

  /* ────────────────── STUDENTS ────────────────── */

  public List<StudentResponseDTO> getAllStudents(Boolean onlyActive) {
    List<Student> list = onlyActive == null
      ? studentRepo.findAll()
      : studentRepo.findByActive(onlyActive);

    return list.stream().map(this::toStudentDTO).toList();
  }

  /* ────────────────── MENTORS ─────────────────── */

  public List<MentorResponseDTO> getAllMentors(Boolean onlyActive) {
    List<Mentor> list = onlyActive == null
      ? mentorRepo.findAll()
      : mentorRepo.findByActive(onlyActive);

    return list.stream().map(this::toMentorDTO).toList();
  }

  /* ────────────────── ADMINS & AGGREGATE ──────── */

  public List<AdminResponseDTO> getAllAdmins() {
    return adminRepo.findAll().stream()
      .map(this::toAdminDTO).toList();
  }

  public AllUsersResponseDTO getAllUsersGrouped() {
    return new AllUsersResponseDTO(
      getAllStudents(null),
      getAllMentors(null),
      getAllAdmins()
    );
  }

  /* ────────────────── DTO MAPPERS ─────────────── */

  private StudentResponseDTO toStudentDTO(Student s) {
    return new StudentResponseDTO(
      s.getId().toHexString(),
      s.getFullName(),
      decrypt(s.getEmail()),
      decrypt(s.getPhoneNumber()),
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
    return new MentorResponseDTO(
      m.getId().toHexString(),
      m.getFullName(),
      decrypt(m.getEmail()),
      decrypt(m.getPhoneNumber()),
      m.getSchool(),
      m.getDepartment(),
      m.getPlacement(),
      m.getArea(),
      m.getBirthDate(),
      m.getIban(),
      m.getNotes(),
      m.isActive(),
      m.getAbandonmentDate()
    );
  }

  private AdminResponseDTO toAdminDTO(Admin a) {
    return new AdminResponseDTO(
      a.getId().toHexString(),
      a.getFullName(),
      decrypt(a.getEmail()),
      decrypt(a.getPhoneNumber())
    );
  }

  private String decrypt(String enc) {
    return enc != null ? encryption.decrypt(enc) : null;
  }
}
