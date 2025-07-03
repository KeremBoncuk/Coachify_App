package io.coachify.service.admin;

import io.coachify.dto.admin.UpdateAdminRequest;
import io.coachify.dto.admin.UpdateMentorRequest;
import io.coachify.dto.admin.UpdateStudentRequest;
import io.coachify.entity.user.Admin;
import io.coachify.entity.user.Mentor;
import io.coachify.entity.user.Student;
import io.coachify.exception.DuplicateFullNameException;
import io.coachify.exception.InvalidEnumException;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserUpdateService {

  private final StudentRepository studentRepository;
  private final MentorRepository mentorRepository;
  private final AdminRepository adminRepository;
  private final EncryptionUtil encryptionUtil;

  public void updateStudent(UpdateStudentRequest dto) {
    ObjectId studentId = new ObjectId(dto.getId());
    Student student = studentRepository.findById(studentId)
      .orElseThrow(() -> new RuntimeException("Student not found"));

    String fullName = dto.getFullName().trim();
    Optional<Student> other = studentRepository.findByFullName(fullName);
    if (other.isPresent() && !other.get().getId().equals(studentId)) {
      throw new DuplicateFullNameException(fullName);
    }

    if (dto.getPaymentStatus() == null) {
      throw new InvalidEnumException("PaymentStatus", "null or unknown");
    }

    student.setFullName(fullName);
    student.setEmail(encryptionUtil.encrypt(dto.getEmail()));
    student.setPhoneNumber(encryptionUtil.encrypt(dto.getPhoneNumber()));

    if (dto.getAssignedMentor() != null) {
      student.setAssignedMentor(new ObjectId(dto.getAssignedMentor()));
    } else {
      student.setAssignedMentor(null);
    }

    student.setPurchaseDate(dto.getPurchaseDate());
    student.setSubscriptionStartDate(dto.getSubscriptionStartDate());
    student.setNextPaymentDate(dto.getNextPaymentDate());
    student.setPaymentStatus(dto.getPaymentStatus());
    student.setNotes(dto.getNotes());
    student.setMentorChangeHistory(dto.getMentorChangeHistory());
    student.setActive(dto.isActive());
    student.setAbandonmentDate(dto.getAbandonmentDate());

    studentRepository.save(student);
  }

  public void updateMentor(UpdateMentorRequest dto) {
    ObjectId mentorId = new ObjectId(dto.getId());
    Mentor mentor = mentorRepository.findById(mentorId)
      .orElseThrow(() -> new RuntimeException("Mentor not found"));

    String fullName = dto.getFullName().trim();
    Optional<Mentor> other = mentorRepository.findByFullName(fullName);
    if (other.isPresent() && !other.get().getId().equals(mentorId)) {
      throw new DuplicateFullNameException(fullName);
    }

    if (dto.getArea() == null) {
      throw new InvalidEnumException("SubjectArea", "null or unknown");
    }

    mentor.setFullName(fullName);
    mentor.setEmail(encryptionUtil.encrypt(dto.getEmail()));
    mentor.setPhoneNumber(encryptionUtil.encrypt(dto.getPhoneNumber()));
    mentor.setSchool(dto.getSchool());
    mentor.setDepartment(dto.getDepartment());
    mentor.setPlacement(dto.getPlacement());
    mentor.setArea(dto.getArea());
    mentor.setBirthDate(dto.getBirthDate());
    mentor.setIban(dto.getIban());
    mentor.setNotes(dto.getNotes());

    if (dto.getAssignedStudents() != null) {
      mentor.setAssignedStudents(dto.getAssignedStudents().stream().map(ObjectId::new).toList());
    } else {
      mentor.setAssignedStudents(List.of());
    }

    mentor.setActive(dto.isActive());
    mentor.setAbandonmentDate(dto.getAbandonmentDate());

    mentorRepository.save(mentor);
  }

  public void updateAdmin(UpdateAdminRequest dto) {
    ObjectId adminId = new ObjectId(dto.getId());
    Admin admin = adminRepository.findById(adminId)
      .orElseThrow(() -> new RuntimeException("Admin not found"));

    String fullName = dto.getFullName().trim();
    Optional<Admin> other = adminRepository.findByFullName(fullName);
    if (other.isPresent() && !other.get().getId().equals(adminId)) {
      throw new DuplicateFullNameException(fullName);
    }

    admin.setFullName(fullName);
    admin.setEmail(encryptionUtil.encrypt(dto.getEmail()));
    admin.setPhoneNumber(encryptionUtil.encrypt(dto.getPhoneNumber()));

    adminRepository.save(admin);
  }
}
