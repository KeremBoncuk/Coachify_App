import {
  Box,
  Divider,
  TextField,
  FormControl,
  Select,
  MenuItem,
  Stack,
} from "@mui/material";
import { useEffect, useState, useCallback } from "react";

import AdminChatRoomList     from "../../../components/chat/AdminChatRoomList";
import AdminChatMessageList  from "../../../components/chat/AdminChatMessageList";
import AdminChatMessageInput from "../../../components/chat/AdminChatMessageInput";

import {
  getChatRooms,
  getMessages,
  sendMessage,
} from "../../../api/adminChat";

const ChatMonitorPage = () => {
  /* ─── STATE ───────────────────────────────────────── */
  const [chatRooms, setChatRooms]           = useState([]);
  const [roomsLoading, setRoomsLoading]     = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  const [messages, setMessages]           = useState([]);
  const [messagesLoading, setMessagesLoading] = useState(false);

  const [searchTerm, setSearchTerm]   = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");

  /* ─── HELPERS ─────────────────────────────────────── */
  const loadRooms = async () => {
    setRoomsLoading(true);
    try {
      const params = {};
      if (statusFilter === "ACTIVE")   params.onlyActive = true;
      if (statusFilter === "INACTIVE") params.onlyActive = false;

      const { data } = await getChatRooms(params);
      setChatRooms(data);
    } finally {
      setRoomsLoading(false);
    }
  };

  const loadMessages = useCallback(async (roomId) => {
    if (!roomId) {
      setMessages([]);
      return;
    }
    setMessagesLoading(true);
    try {
      const { data } = await getMessages(roomId);
      /* backend returns newest-first → flip to oldest-first */
      setMessages(data.slice().reverse());
    } finally {
      setMessagesLoading(false);
    }
  }, []);

  const handleSend = async (text) => {
    if (!selectedRoomId) return;
    await sendMessage(selectedRoomId, text);
    await loadMessages(selectedRoomId);     // refresh list
  };

  /* ─── EFFECTS ─────────────────────────────────────── */
  useEffect(() => { loadRooms(); }, [statusFilter]);
  useEffect(() => { loadMessages(selectedRoomId); }, [selectedRoomId, loadMessages]);

  /* ─── FILTERED ROOMS ─────────────────────────────── */
  const visibleRooms = chatRooms.filter((r) =>
    `${r.mentorFullName} ${r.studentFullName}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  /* ─── RENDER ─────────────────────────────────────── */
  return (
    <Box display="flex" flex={1} sx={{ minHeight: 0 }}>
      {/* LEFT – ROOMS */}
      <Box
        width={320}
        flexShrink={0}
        borderRight="1px solid #ddd"
        display="flex"
        flexDirection="column"
      >
        {/* filters */}
        <Stack direction="row" spacing={1} p={1}>
          <TextField
            fullWidth
            size="small"
            placeholder="Search…"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <FormControl size="small" sx={{ minWidth: 110 }}>
            <Select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="INACTIVE">Inactive</MenuItem>
              <MenuItem value="ANY">Any</MenuItem>
            </Select>
          </FormControl>
        </Stack>
        <Divider />
        <Box flex={1} sx={{ overflowY: "auto" }}>
          <AdminChatRoomList
            chatRooms={visibleRooms}
            selectedId={selectedRoomId}
            onSelect={setSelectedRoomId}
            loading={roomsLoading}
          />
        </Box>
      </Box>

      {/* RIGHT – MESSAGES */}
      <Box display="flex" flexDirection="column" flex={1} sx={{ minHeight: 0 }}>
        {/* scrollable list */}
        <Box flex={1} sx={{ overflowY: "auto" }}>
          <AdminChatMessageList
            messages={messages}
            loading={messagesLoading}
          />
        </Box>

        {/* input (fixed) */}
        <Divider />
        <AdminChatMessageInput
          disabled={!selectedRoomId}
          onSend={handleSend}
        />
      </Box>
    </Box>
  );
};

export default ChatMonitorPage;
