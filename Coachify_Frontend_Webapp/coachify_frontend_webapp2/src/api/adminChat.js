/**
 * Admin Chat API helper
 * ------------------------------------------------
 * Uses the shared Axios instance (adds JWT automatically).
 * All endpoints align with ChatMessageAdminController.
 */

import axios from "./axiosInstance";

/* ─────────────── Chat-rooms ─────────────── */

// GET /admin/chat/get-chatrooms
//   params: { onlyActive: true|false }  // optional
export const getChatRooms = (params = {}) =>
  axios.get("/admin/chat/get-chatrooms", { params });

/* ─────────────── Messages (paginated) ─────────────── */

// GET /admin/chat/get-messages/page
//   params: { chatRoomId, before, limit }
export const getMessagesPage = (chatRoomId, before = null, limit = 20) =>
  axios.get("/admin/chat/get-messages/page", {
    params: { chatRoomId, before, limit },
  });

/* ─────────────── Send message ─────────────── */

// POST /admin/chat/send-message
//   body  : { text, mediaUrls }
//   params: { chatRoomId }
export const sendMessage = (chatRoomId, text, mediaUrls = []) =>
  axios.post(
    "/admin/chat/send-message",
    { text, mediaUrls },
    { params: { chatRoomId } }
  );
