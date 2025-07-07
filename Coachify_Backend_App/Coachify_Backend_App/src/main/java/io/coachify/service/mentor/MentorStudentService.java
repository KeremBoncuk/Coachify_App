package io.coachify.service.mentor;

import io.coachify.dto.mentor.MentorStudentResponseDTO;
import io.coachify.entity.user.Student;
import io.coachify.repo.StudentRepository;
import io.coachify.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorStudentService {

  private final StudentRepository studentRepository;
  private final EncryptionUtil encryptionUtil;

  public List<MentorStudentResponseDTO> getAssignedStudents(ObjectId mentorId) {
    // Debug: mentorId received
    System.out.println("🔍 MentorStudentService → Fetching students for Mentor ID: " + mentorId);

    List<Student> students = studentRepository.findByAssignedMentor(mentorId);

    // Debug: number of students found + names
    System.out.println("🔍 MentorStudentService → Found " + students.size() + " students.");
    students.forEach(s -> System.out.println("   - " + s.getFullName()));

    List<MentorStudentResponseDTO> dtos = students.stream()
      .map(this::toDTO)
      .toList();

    // ✅ Debug: exactly what’s sent to frontend
    System.out.println("🔍 MentorStudentService → Sending DTOs to frontend:");
    for (MentorStudentResponseDTO dto : dtos) {
      System.out.printf(
        """
        ⚙️  DTO:
            ID                  : %s
            Name                : %s
            Email               : %s
            Phone               : %s
            Payment             : %s
            Purchase Date       : %s
            Start Date          : %s
            Next Payment        : %s
            Active              : %s
        """,
        dto.getId(),
        dto.getFullName(),
        dto.getEmail(),
        dto.getPhoneNumber(),
        dto.getPaymentStatus(),
        dto.getPurchaseDate(),
        dto.getSubscriptionStartDate(),
        dto.getNextPaymentDate(),
        dto.isActive()
      );
    }

    return dtos;
  }

  private MentorStudentResponseDTO toDTO(Student student) {
    return new MentorStudentResponseDTO(
      student.getId().toHexString(),
      student.getFullName(),
      decryptSafely(student.getEmail()),
      decryptSafely(student.getPhoneNumber()),
      student.getPaymentStatus(),
      student.getPurchaseDate(),
      student.getSubscriptionStartDate(),
      student.getNextPaymentDate(),
      student.isActive()
    );
  }

  private String decryptSafely(String encrypted) {
    if (encrypted == null || encrypted.isBlank()) return "";
    try {
      return encryptionUtil.decrypt(encrypted);
    } catch (Exception ex) {
      System.err.println("⚠️  Decryption failed, returning raw value: " + ex.getMessage());
      return encrypted;
    }
  }
}
