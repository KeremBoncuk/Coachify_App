package io.coachify.service.admin.assign;

import io.coachify.entity.chat.ChatRoom;
import io.coachify.exception.ConflictException;
import io.coachify.repo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatRoomCreationService {

    private final ChatRoomRepository chatRoomRepository;

    public void handleChatRoomsForMentorChange(ObjectId studentId,
                                               ObjectId previousMentorId,
                                               ObjectId newMentorId) {
        if (previousMentorId != null) {
            chatRoomRepository.findByStudentIdAndMentorIdAndIsActiveTrue(studentId, previousMentorId)
                    .ifPresent(room -> {
                        room.setActive(false);
                        chatRoomRepository.save(room);
                    });
        }

        chatRoomRepository.findByStudentIdAndMentorIdAndIsActiveTrue(studentId, newMentorId)
                .ifPresent(room -> {
                    throw new ConflictException("Active chat-room already exists for this student-mentor pair.");
                });

        chatRoomRepository.findByStudentIdAndMentorId(studentId, newMentorId)
                .ifPresentOrElse(room -> {
                    room.setActive(true);
                    chatRoomRepository.save(room);
                }, () -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setStudentId(studentId);
                    newRoom.setMentorId(newMentorId);
                    newRoom.setActive(true);
                    newRoom.setCreatedAt(Instant.now());
                    chatRoomRepository.save(newRoom);
                });
    }

    public void handleUnassignment(ObjectId studentId, ObjectId previousMentorId) {
        if (previousMentorId == null) {
            return;  // No previous mentor, nothing to deactivate
        }

        chatRoomRepository.findByStudentIdAndMentorIdAndIsActiveTrue(studentId, previousMentorId)
                .ifPresent(room -> {
                    room.setActive(false);
                    chatRoomRepository.save(room);
                });
    }
}
