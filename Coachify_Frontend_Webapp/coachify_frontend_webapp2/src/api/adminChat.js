/**
 * Admin Chat API helper
 * ------------------------------------------------
 * Uses the shared Axios instance (with JWT header).
 * Endpoints taken from Postman collection.
 */

import axios from "./axiosInstance"; // ✅ Correct import for your project

// GET /admin/chat/get-chatrooms
export const getChatRooms = async (params = {}) =>
  axios.get("/admin/chat/get-chatrooms", { params });

// GET /admin/chat/get-messages
export const getMessages = async (chatRoomId) =>
  axios.get("/admin/chat/get-messages", { params: { chatRoomId } });

// POST /admin/chat/send-message
export const sendMessage = async (chatRoomId, text) =>
  axios.post("/admin/chat/send-message", { text }, { params: { chatRoomId } });
