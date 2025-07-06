import {
  Box,
  Divider,
  TextField,
  FormControl,
  Select,
  MenuItem,
  Stack,
} from "@mui/material";
import { useEffect, useState, useCallback, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

import ChatRoomList from "./ChatRoomList";
import ChatMessageList from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import { getChatRooms, getMessagesPage } from "../../../api/adminChat";
import { getToken } from "../../../auth/jwtUtils";

const BACKEND = process.env.REACT_APP_BACKEND_URL ?? "http://localhost:8080";
const PAGE_SIZE = 20;

/* STOMP destinations */
const SEND_DEST = "/app/chat.send";
const SEEN_DEST = "/app/chat.seen";
const TOPIC_BASE = "/topic/chat"; // + /{roomId}

/* ───────────────────────── helpers ───────────────────────── */
const safeLower = (str = "") => String(str).toLowerCase();

/* ───────────────────────── component ──────────────────────── */
const ChatMonitorPage = () => {
  /* ───────── rooms state ───────── */
  const [chatRooms, setChatRooms] = useState([]);
  const [roomsLoading, setRoomsLoading] = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  /* ───────── messages state ───────── */
  const [messages, setMessages] = useState([]); // oldest → newest
  const [hasMore, setHasMore] = useState(false);
  const [nextBefore, setNextBefore] = useState(null);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingOlder, setLoadingOlder] = useState(false);

  /* ───────── filters ───────── */
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");

  /* ───────── STOMP client ───────── */
  const stompRef = useRef(null);
  const roomSubRef = useRef(null);
  const seenSubRef = useRef(null);

  /* ─── connect once ─── */
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${BACKEND}/ws`),
      connectHeaders: { Authorization: `Bearer ${getToken()}` },
      reconnectDelay: 5000,
    });
    client.activate();
    stompRef.current = client;
    return () => client.deactivate();
  }, []);

  /* ─── (re)subscribe on room change ─── */
  useEffect(() => {
    if (!stompRef.current || !stompRef.current.connected) return;
    const client = stompRef.current;

    roomSubRef.current?.unsubscribe();
    seenSubRef.current?.unsubscribe();

    if (!selectedRoomId) return;

    roomSubRef.current = client.subscribe(
      `${TOPIC_BASE}/${selectedRoomId}`,
      (msg) => {
        const m = JSON.parse(msg.body);
        setMessages((prev) => [...prev, m]);

        /* mark everything seen by admin right away */
        client.publish({
          destination: SEEN_DEST,
          body: JSON.stringify({
            chatRoomId: selectedRoomId,
            seenUntil: new Date().toISOString(),
          }),
        });
      }
    );

    seenSubRef.current = client.subscribe(
      `${TOPIC_BASE}/${selectedRoomId}/seen`,
      () => {} // no-op for now
    );
  }, [selectedRoomId]);

  /* ─── rooms fetch ─── */
  const loadRooms = useCallback(async () => {
    setRoomsLoading(true);
    try {
      const params = {};
      if (statusFilter === "ACTIVE") params.onlyActive = true;
      if (statusFilter === "INACTIVE") params.onlyActive = false;

      const { data } = await getChatRooms(params);
      setChatRooms(data);
    } finally {
      setRoomsLoading(false);
    }
  }, [statusFilter]);

  /* ─── messages fetch (paged) ─── */
  const fetchPage = async (roomId, before = null) => {
    const { data } = await getMessagesPage(roomId, before, PAGE_SIZE);
    return {
      msgs: data.messages.slice().reverse(),
      hasMore: data.hasMore,
      nextBefore: data.nextBefore,
    };
  };

  const loadInitialMessages = useCallback(
    async (roomId) => {
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
    },
    [] // stable: no deps
  );

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

  const handleSend = (text) => {
    if (!selectedRoomId || !stompRef.current?.connected) return;
    stompRef.current.publish({
      destination: SEND_DEST,
      body: JSON.stringify({
        chatRoomId: selectedRoomId,
        text,
        mediaUrls: [],
      }),
    });
  };

  /* ─── effects ─── */
  useEffect(() => {
    loadRooms();
  }, [loadRooms]);

  useEffect(() => {
    loadInitialMessages(selectedRoomId);
  }, [selectedRoomId, loadInitialMessages]);

  /* ─── derived list: apply search filter (defensive) ─── */
  const visibleRooms = chatRooms.filter((r) =>
    `${r.mentorFullName ?? ""} ${r.studentFullName ?? ""}`
      .toLowerCase()
      .includes(safeLower(searchTerm))
  );

  /* ─── render ─── */
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
