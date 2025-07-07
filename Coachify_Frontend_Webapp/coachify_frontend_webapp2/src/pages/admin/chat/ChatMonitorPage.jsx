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

import ChatRoomList     from "./ChatRoomList";
import ChatMessageList  from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import {
  getChatRooms,
  getMessagesPage,
} from "../../../api/adminChat";
import { useWebSocket } from '../../../hooks/useWebSocket';

const PAGE_SIZE = 20;

const ChatMonitorPage = () => {
  /* ───────────── rooms state ───────────── */
  const [chatRooms, setChatRooms]   = useState([]);
  const [roomsLoading, setRoomsLoading] = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  /* ───────────── messages state ───────────── */
  const [messages, setMessages] = useState([]);          // oldest→newest
  const [hasMore, setHasMore]   = useState(false);
  const [nextBefore, setNextBefore] = useState(null);    // cursor
  const [messagesLoading, setMessagesLoading]     = useState(false);
  const [loadingOlder, setLoadingOlder]           = useState(false);

  /* ───────────── filters ───────────── */
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");

  /* ───────────── rooms fetch ───────────── */
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

  /* ───────────── messages fetch (page) ───────────── */
  const fetchPage = async (roomId, before = null) => {
    const { data } = await getMessagesPage(roomId, before, PAGE_SIZE);
    /* backend returns newest→first  → flip */
    return {
      msgs      : data.messages.slice().reverse(),
      hasMore   : data.hasMore,
      nextBefore: data.nextBefore,
    };
  };

  /** first load when room changes */
  const loadInitialMessages = useCallback(async (roomId) => {
    if (!roomId) {
      setMessages([]);
      setHasMore(false);
      setNextBefore(null);
      return;
    }
    setMessagesLoading(true);
    try {
      const { msgs, hasMore, nextBefore } = await fetchPage(roomId);
      setMessages(msgs);
      setHasMore(hasMore);
      setNextBefore(nextBefore);
    } finally {
      setMessagesLoading(false);
    }
  }, []);

  /** older page prepend */
  const loadOlder = async () => {
    if (!hasMore || loadingOlder || !selectedRoomId) return;

    setLoadingOlder(true);
    try {
      const { msgs, hasMore: hm, nextBefore: nb } = await fetchPage(
        selectedRoomId,
        nextBefore
      );
      setMessages((prev) => [...msgs, ...prev]);
      setHasMore(hm);
      setNextBefore(nb);
    } finally {
      setLoadingOlder(false);
    }
  };

  /* ───────────── WebSocket ───────────── */
  const { sendMessage: sendWsMessage } = useWebSocket(
    selectedRoomId ? `/topic/chat/${selectedRoomId}` : null,
    useCallback((newMessage) => {
      setMessages((prev) => [...prev, newMessage]);
    }, [])
  );

  /* ───────────── send ───────────── */
  const handleSend = async (text) => {
    if (!selectedRoomId) return;
    sendWsMessage("/app/chat.sendMessage", {
      chatRoomId: selectedRoomId,
      text,
      mediaUrls: [],
    });
  };

  /* ───────────── effects ───────────── */
  useEffect(() => { loadRooms(); }, [statusFilter]);
  useEffect(() => { loadInitialMessages(selectedRoomId); },
           [selectedRoomId, loadInitialMessages]);

  /* ───────────── rooms search filter ───────────── */
  const visibleRooms = chatRooms.filter((r) =>
    `${r.mentorFullName} ${r.studentFullName}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  /* ───────────── render ───────────── */
  return (
    <Box display="flex" flex={1} sx={{ minHeight: 0 }}>
      {/* LEFT ─ Rooms list */}
      <Box
        width={320}
        flexShrink={0}
        borderRight="1px solid #ddd"
        display="flex"
        flexDirection="column"
      >
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
          <ChatRoomList
            chatRooms={visibleRooms}
            selectedId={selectedRoomId}
            onSelect={setSelectedRoomId}
            loading={roomsLoading}
          />
        </Box>
      </Box>

      {/* RIGHT ─ Messages */}
      <Box display="flex" flexDirection="column" flex={1} sx={{ minHeight: 0 }}>
        <ChatMessageList
          messages={messages}
          loading={messagesLoading}
          hasMore={hasMore}
          loadingOlder={loadingOlder}
          loadOlder={loadOlder}
        />

        <Divider />
        <ChatMessageInput disabled={!selectedRoomId} onSend={handleSend} />
      </Box>
    </Box>
  );
};

export default ChatMonitorPage;
