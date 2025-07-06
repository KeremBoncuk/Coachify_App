import axios from "./axiosInstance";

/* ───────────── Rooms (active only) ───────────── */
/* GET /mentor/chat/rooms                          */
export const getActiveChatRooms = () =>
  axios.get("/mentor/chat/rooms");

/* ───────────── Message history (paged) ────────── */
/* GET /chat/history?roomId=&before=&limit=        */
export const getPaginatedMessages = (
  chatRoomId,
  before = null,
  limit  = 20
) =>
  axios.get("/chat/history", {
    params: { roomId: chatRoomId, before, limit },
  });
