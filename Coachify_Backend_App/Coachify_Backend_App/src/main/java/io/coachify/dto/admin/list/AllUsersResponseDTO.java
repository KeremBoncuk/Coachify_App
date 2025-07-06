package io.coachify.dto.admin.list;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllUsersResponseDTO {
  private List<StudentResponseDTO> students;
  private List<MentorResponseDTO> mentors;
  private List<AdminResponseDTO> admins;
}
