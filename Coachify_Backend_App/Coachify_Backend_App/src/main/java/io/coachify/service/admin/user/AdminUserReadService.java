package io.coachify.service.admin.user;

import io.coachify.dto.admin.list.AdminResponseDTO;
import io.coachify.dto.admin.list.MentorResponseDTO;
import io.coachify.dto.admin.list.StudentResponseDTO;
import io.coachify.entity.user.Admin;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserReadService {

  private final StudentRepository studentRepository;
  private final MentorRepository mentorRepository;
  private final AdminRepository adminRepository;
  private final EncryptionUtil encryptionUtil;

  public StudentResponseDTO getStudentById(String id) {
    Student student = studentRepository.findById(new ObjectId(id))
      .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

    return new StudentResponseDTO(
      student.getId().toHexString(),
      student.getFullName(),
      student.getEmail(),
      student.getPhoneNumber(),
      student.getAssignedMentor() != null ? student.getAssignedMentor().toHexString() : null,
      student.getPurchaseDate(),
      student.getSubscriptionStartDate(),
      student.getNextPaymentDate(),
      student.getPaymentStatus(),
      student.getNotes(),
      student.getMentorChangeHistory(),
      student.isActive(),
      student.getAbandonmentDate()
    );
  }

  public MentorResponseDTO getMentorById(String id) {
    Mentor mentor = mentorRepository.findById(new ObjectId(id))
      .orElseThrow(() -> new RuntimeException("Mentor not found with id: " + id));

    return new MentorResponseDTO(
      mentor.getId().toHexString(),
      mentor.getFullName(),
      mentor.getEmail(),
      mentor.getPhoneNumber(),
      mentor.getSchool(),
      mentor.getDepartment(),
      mentor.getPlacement(),
      mentor.getArea(),
      mentor.getBirthDate(),
      mentor.getIban(),
      mentor.getNotes(),
      mentor.isActive(),
      mentor.getAbandonmentDate()
    );
  }

  public AdminResponseDTO getAdminById(String id) {
    Admin admin = adminRepository.findById(new ObjectId(id))
      .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

    return new AdminResponseDTO(
      admin.getId().toHexString(),
      admin.getFullName(),
      admin.getEmail(),
      admin.getPhoneNumber()
    );
  }
}
