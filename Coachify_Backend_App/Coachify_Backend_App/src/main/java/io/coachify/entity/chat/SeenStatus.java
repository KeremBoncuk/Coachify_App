package io.coachify.entity.chat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeenStatus {
  private boolean seenByStudent;
  private boolean seenByMentor;
}
