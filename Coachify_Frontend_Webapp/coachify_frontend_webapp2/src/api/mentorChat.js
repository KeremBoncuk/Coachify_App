// src/api/mentorChat.js
import axiosInstance from "./axiosInstance";

/**
 * Get all active chat rooms for the logged-in mentor.
 */
export const getActiveChatRooms = () => {
  return axiosInstance.get("/mentor/chat/rooms");
};

/**
 * Send a message to a specific chat room.
 * @param {string} chatRoomId - The chat room ID.
 * @param {string} text - Message text.
 * @param {string[]} mediaUrls - Array of media URLs (optional).
 */
export const sendMessage = (chatRoomId, text, mediaUrls = []) => {
  return axiosInstance.post("/mentor/chat/send-message", {
    chatRoomId,
    text,
    mediaUrls,
  });
};

/**
 * Get all messages for a chat room.
 * @param {string} chatRoomId - The chat room ID.
 */
export const getAllMessages = (chatRoomId) => {
  return axiosInstance.get("/mentor/chat/messages/all", {
    params: { chatRoomId },
  });
};

/**
 * Get paginated messages for a chat room.
 * @param {string} chatRoomId - The chat room ID.
 * @param {string} [before] - Optional ISO date string to fetch messages before.
 * @param {number} [limit=20] - Number of messages to fetch (default: 20).
 */
export const getPaginatedMessages = (chatRoomId, before, limit = 20) => {
  return axiosInstance.get("/mentor/chat/messages", {
    params: { chatRoomId, before, limit },
  });
};

/**
 * Mark messages as seen in a chat room.
 * @param {string} chatRoomId - The chat room ID.
 * @param {string} seenUntil - ISO date string indicating up to which message is seen.
 */
export const markMessagesAsSeen = (chatRoomId, seenUntil) => {
  return axiosInstance.post("/mentor/chat/messages/seen", null, {
    params: { chatRoomId, seenUntil },
  });
};
