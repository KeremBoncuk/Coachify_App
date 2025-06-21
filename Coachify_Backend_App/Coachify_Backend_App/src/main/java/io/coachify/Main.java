package io.coachify;

import io.coachify.entity.user.Admin;
import io.coachify.repo.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner {

  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(String... args) {
    if (adminRepository.count() == 0) {
      Admin devAdmin = new Admin();
      devAdmin.setId(new ObjectId());
      devAdmin.setName("Dev");
      devAdmin.setSurname("Admin");
      devAdmin.setFullName("Dev Admin");
      devAdmin.setEmail("N/A"); // Encrypted fields skipped
      devAdmin.setPhoneNumber("N/A");
      devAdmin.setHashedPassword(passwordEncoder.encode("admin123"));

      adminRepository.save(devAdmin);
      System.out.println("Dev admin user created with fullName: Dev Admin and password: admin123");
    } else {
      System.out.println("Admin user(s) already exist. No dev admin created.");
    }
  }
}
