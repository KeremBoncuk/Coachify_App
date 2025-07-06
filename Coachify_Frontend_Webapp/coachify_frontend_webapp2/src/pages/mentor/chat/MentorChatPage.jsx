import {
  Box, Divider, TextField, Stack
} from "@mui/material";
import { useEffect, useState, useCallback, useRef } from "react";

import SockJS  from "sockjs-client";
import { Client } from "@stomp/stompjs";

import ChatRoomList     from "./ChatRoomList";
import ChatMessageList  from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import {
  getActiveChatRooms,
  getPaginatedMessages
} from "../../../api/mentorChat";
import { getStudentById } from "../../../api/adminUsers";
import { getToken }       from "../../../auth/jwtUtils";

const BACKEND   = process.env.REACT_APP_BACKEND_URL ?? "http://localhost:8080";
const PAGE_SIZE  = 20;
/* STOMP destinations */
const SEND_DEST  = "/app/chat.send";
const SEEN_DEST  = "/app/chat.seen";
const TOPIC_BASE = "/topic/chat";          // + /{roomId}

const MentorChatPage = () => {
  /* ───────── rooms ───────── */
  const [chatRooms,     setChatRooms]     = useState([]);
  const [roomsLoading,  setRoomsLoading]  = useState(true);
  const [selectedRoomId,setSelectedRoomId]= useState(null);

  /* ───────── messages ───────── */
  const [messages, setMessages]            = useState([]);
  const [hasMore,  setHasMore]             = useState(false);
  const [nextBefore,setNextBefore]         = useState(null);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingOlder,    setLoadingOlder]    = useState(false);

  /* ───────── search ───────── */
  const [searchTerm, setSearchTerm] = useState("");

  /* ───────── STOMP client ───────── */
  const stompRef   = useRef(null);
  const roomSubRef = useRef(null);
  const seenSubRef = useRef(null);

  /* connect once */
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${BACKEND}/ws`),
      connectHeaders  : { Authorization: `Bearer ${getToken()}` },
      reconnectDelay  : 5000,
    });
    client.activate();
    stompRef.current = client;
    return () => client.deactivate();
  }, []);

  /* (re)subscribe when room changes */
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
        client.publish({
          destination: SEEN_DEST,
          body: JSON.stringify({
            chatRoomId: selectedRoomId,
            seenUntil : new Date().toISOString(),
          }),
        });
      }
    );

    seenSubRef.current = client.subscribe(
      `${TOPIC_BASE}/${selectedRoomId}/seen`,
      () => {}
    );
  }, [selectedRoomId]);

  /* ───────── rooms fetch ───────── */
  const loadRooms = async () => {
    setRoomsLoading(true);
    try {
      const { data } = await getActiveChatRooms();

      const enriched = await Promise.all(
        data.map(async (r) => {
          if (r.studentFullName) return r;
          try {
            const stu = await getStudentById(r.studentId);
            return { ...r, studentFullName: stu.fullName };
          } catch {
            return r;
          }
        })
      );

      setChatRooms(enriched);
    } finally {
      setRoomsLoading(false);
    }
  };

  /* ───────── history fetch ───────── */
  const fetchPage = async (roomId, before = null) => {
    const { data } = await getPaginatedMessages(roomId, before, PAGE_SIZE);
    return {
      msgs      : data.messages.slice().reverse(),
      hasMore   : data.hasMore,
      nextBefore: data.nextBefore,
    };
  };

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

      stompRef.current?.publish({
        destination: SEEN_DEST,
        body: JSON.stringify({
          chatRoomId: roomId,
          seenUntil : new Date().toISOString(),
        }),
      });
    } finally {
      setMessagesLoading(false);
    }
  }, []);

  const loadOlder = async () => {
    if (!hasMore || loadingOlder || !selectedRoomId) return;
    setLoadingOlder(true);
    try {
      const { msgs, hasMore: hm, nextBefore: nb } = await fetchPage(
        selectedRoomId, nextBefore
      );
      setMessages((prev) => [...msgs, ...prev]);
      setHasMore(hm);
      setNextBefore(nb);
    } finally {
      setLoadingOlder(false);
    }
  };

  /* send */
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

  /* effects */
  useEffect(() => { loadRooms(); }, []);
  useEffect(() => { loadInitialMessages(selectedRoomId); },
           [selectedRoomId, loadInitialMessages]);

  /* filter rooms */
  const roomsVisible = chatRooms.filter((r) =>
    (r.studentFullName || "").toLowerCase().includes(searchTerm.toLowerCase())
  );

  /* render */
  return (
    <Box display="flex" flex={1} sx={{ minHeight: 0 }}>
      {/* LEFT ─ Rooms */}
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
        </Stack>
        <Divider />
        <Box flex={1} sx={{ overflowY: "auto" }}>
          <ChatRoomList
            chatRooms={roomsVisible}
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

export default MentorChatPage;
