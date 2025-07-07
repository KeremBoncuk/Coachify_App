import axios from "./axiosInstance";

/* ───────────── Rooms ───────────── */

export const getActiveChatRooms = () =>
  axios.get("/mentor/chat/rooms");

/* ───────────── Messages (paginated) ───────────── */

export const getPaginatedMessages = (
  chatRoomId,
  before = null,
  limit = 20
) =>
  axios.get("/mentor/chat/messages", {
    params: { chatRoomId, before, limit },
  });



/* ───────────── Mark as seen ───────────── */

export const markMessagesAsSeen = (chatRoomId, seenUntilIso) =>
  axios.post(
    "/mentor/chat/messages/seen",
    null,
    { params: { chatRoomId, seenUntil: seenUntilIso } }
  );
