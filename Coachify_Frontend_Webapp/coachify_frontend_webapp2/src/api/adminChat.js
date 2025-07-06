/**
 * Admin Chat API helper (REST side)
 * ---------------------------------
 *  • Chat-rooms list
 *  • Paged history for “infinite scroll”
 *
 *  New real-time messages & seen-acks are handled via WebSocket
 *  (see ChatMonitorPage.jsx).
 */

import axios from "./axiosInstance";

/* ─────────────── Chat-rooms ──────────────── */
/* GET /admin/chat/rooms?onlyActive=true|false */
export const getChatRooms = (params = {}) =>
  axios.get("/admin/chat/rooms", { params });

/* ─────────────── Message history ──────────── */
/* GET /chat/history?roomId=&before=&limit=    */
export const getMessagesPage = (
  chatRoomId,
  before = null,
  limit  = 20
) =>
  axios.get("/chat/history", {
    params: { roomId: chatRoomId, before, limit },
  });
