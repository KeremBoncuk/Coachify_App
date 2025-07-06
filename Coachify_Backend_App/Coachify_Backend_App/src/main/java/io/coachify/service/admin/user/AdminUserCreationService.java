package io.coachify.service.admin.user;

import io.coachify.dto.admin.register.RegisterAdminRequest;
import io.coachify.dto.admin.register.RegisterMentorRequest;
import io.coachify.dto.admin.register.RegisterStudentRequest;
import io.coachify.entity.user.*;
import io.coachify.repo.AdminRepository;
import io.coachify.repo.MentorRepository;
import io.coachify.repo.StudentRepository;
import io.coachify.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminUserCreationService {

  private final StudentRepository studentRepository;
  private final MentorRepository mentorRepository;
  private final AdminRepository adminRepository;

  private final PasswordEncoder passwordEncoder;
  private final EncryptionUtil encryptionUtil;

  private void checkFullNameUniqueness(String fullName) {
    boolean exists =
      studentRepository.findByFullName(fullName).isPresent() ||
        mentorRepository.findByFullName(fullName).isPresent() ||
        adminRepository.findByFullName(fullName).isPresent();

    if (exists) {
      throw new IllegalArgumentException("A user with fullName '" + fullName + "' already exists.");
    }
  }

  public ObjectId registerStudent(RegisterStudentRequest req) {
    String fullName = req.getFullName().trim();
    checkFullNameUniqueness(fullName);

    Student student = new Student();
    student.setId(new ObjectId());
    student.setFullName(fullName);

    student.setEmail(encryptionUtil.encrypt(req.getEmail()));
    student.setPhoneNumber(encryptionUtil.encrypt(req.getPhoneNumber()));
    student.setHashedPassword(passwordEncoder.encode(req.getPassword()));

    student.setAssignedMentor(null);
    student.setPurchaseDate(req.getPurchaseDate());
    student.setSubscriptionStartDate(req.getSubscriptionStartDate());
    student.setNextPaymentDate(req.getNextPaymentDate());
    student.setPaymentStatus(req.getPaymentStatus());
    student.setNotes(req.getNotes());

    student.setMentorChangeHistory(Collections.emptyList());
    student.setActive(true);
    student.setAbandonmentDate(null);

    studentRepository.save(student);
    return student.getId();
  }

  public ObjectId registerMentor(RegisterMentorRequest req) {
    String fullName = req.getFullName().trim();
    checkFullNameUniqueness(fullName);

    Mentor mentor = new Mentor();
    mentor.setId(new ObjectId());
    mentor.setFullName(fullName);

    mentor.setEmail(encryptionUtil.encrypt(req.getEmail()));
    mentor.setPhoneNumber(encryptionUtil.encrypt(req.getPhoneNumber()));
    mentor.setHashedPassword(passwordEncoder.encode(req.getPassword()));

    mentor.setSchool(req.getSchool());
    mentor.setDepartment(req.getDepartment());
    mentor.setPlacement(req.getPlacement());
    mentor.setArea(req.getArea());
    mentor.setBirthDate(req.getBirthDate());
    mentor.setIban(req.getIban());
    mentor.setNotes(req.getNotes());

    mentor.setActive(true);
    mentor.setAbandonmentDate(null);

    mentorRepository.save(mentor);
    return mentor.getId();
  }

  public ObjectId registerAdmin(RegisterAdminRequest req) {
    String fullName = req.getFullName().trim();
    checkFullNameUniqueness(fullName);

    Admin admin = new Admin();
    admin.setId(new ObjectId());
    admin.setFullName(fullName);

    admin.setEmail(encryptionUtil.encrypt(req.getEmail()));
    admin.setPhoneNumber(encryptionUtil.encrypt(req.getPhoneNumber()));
    admin.setHashedPassword(passwordEncoder.encode(req.getPassword()));

    adminRepository.save(admin);
    return admin.getId();
  }
}
