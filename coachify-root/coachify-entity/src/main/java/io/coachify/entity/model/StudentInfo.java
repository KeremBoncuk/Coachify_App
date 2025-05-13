package io.coachify.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
  private String ogrenciAdi;
  private String mentorununAdi;
  private String sinifi;
  private String sag;
  private String ubg;
  private String ub;
  private String ay;
  private String bso;
  private String ayi;
  private String odemeDurumu;
  private String odeme;
  private String biraktigiAy;
  private String durum;
  private String aranmaGunu;
  private String aranmaAyi;
  private String mentorDegisimi;
  private String iadeDurumu;
  private String birakmaNedeni;
  private String mevcutNet;
  private String hedefNet;
  private String ozelAciklama;
  private String telNo;
}
