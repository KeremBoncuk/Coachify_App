package io.coachify.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class EncryptionUtil {

  @Value("${encryption.secret-key}")
  private String secretKeyBase64;

  private SecretKeySpec secretKeySpec;

  private static final String AES = "AES";
  private static final String AES_GCM = "AES/GCM/NoPadding";
  private static final int GCM_TAG_LENGTH = 128;
  private static final int IV_LENGTH = 12;

  private final SecureRandom secureRandom = new SecureRandom();

  @PostConstruct
  public void init() {
    byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
    this.secretKeySpec = new SecretKeySpec(decodedKey, AES);
  }

  public String encrypt(String plainText) {
    try {
      byte[] iv = new byte[IV_LENGTH];
      secureRandom.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(AES_GCM);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, spec);

      byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
      System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
      System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

      return Base64.getEncoder().encodeToString(encryptedWithIv);
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  public String decrypt(String encryptedBase64) {
    try {
      byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedBase64);
      byte[] iv = new byte[IV_LENGTH];
      byte[] encrypted = new byte[encryptedWithIv.length - IV_LENGTH];

      System.arraycopy(encryptedWithIv, 0, iv, 0, IV_LENGTH);
      System.arraycopy(encryptedWithIv, IV_LENGTH, encrypted, 0, encrypted.length);

      Cipher cipher = Cipher.getInstance(AES_GCM);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, spec);

      byte[] decrypted = cipher.doFinal(encrypted);
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Decryption failed", e);
    }
  }
}
