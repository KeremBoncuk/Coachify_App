package io.coachify.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachInfo {
  private String adSoyad;
  private String telefonNo;
  private String okul;
  private String siralama;
  private String alan;
  private String dogumTarihi;
  private String ibanHesap;
  private String ismi;
  private String mail;
}
