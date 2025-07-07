// MentorChatPage.jsx
import {
  Box,
  Divider,
  TextField,
  Stack,
} from "@mui/material";
import { useEffect, useState, useCallback, useRef } from "react";

import ChatRoomList     from "./ChatRoomList";
import ChatMessageList  from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import {
  getActiveChatRooms,
  getPaginatedMessages,
  sendMessage,
  markMessagesAsSeen,
} from "../../../api/mentorChat";
import { getStudentById } from "../../../api/adminUsers";

const PAGE_SIZE     = 20;
const POLL_INTERVAL = 2000;

const MentorChatPage = () => {
  const [chatRooms, setChatRooms] = useState([]);
  const [roomsLoading, setRoomsLoading] = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  const [messages, setMessages] = useState([]);
  const [hasMore, setHasMore]   = useState(false);
  const [nextBefore, setNextBefore] = useState(null);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingOlder, setLoadingOlder]       = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const containerRef = useRef(null);
  const pollingRef   = useRef(null);

  const loadRooms = async () => {
    setRoomsLoading(true);
    try {
      const { data } = await getActiveChatRooms();
      const enriched = await Promise.all(
        data.map(async r => {
          if (r.studentFullName) return r;
          try {
            const stu = await getStudentById(r.studentId);
            return { ...r, studentFullName: stu.fullName };
          } catch { return r; }
        })
      );
      setChatRooms(enriched);
    } finally { setRoomsLoading(false); }
  };

  const fetchPage = async (roomId, before = null) => {
    const { data } = await getPaginatedMessages(roomId, before, PAGE_SIZE);
    return {
      msgs      : data.messages.slice().reverse(),
      hasMore   : data.hasMore,
      nextBefore: data.nextBefore,
    };
  };

  const loadInitialMessages = useCallback(async roomId => {
    if (!roomId) {
      setMessages([]); setHasMore(false); setNextBefore(null);
      return;
    }
    setMessagesLoading(true);
    try {
      const { msgs, hasMore, nextBefore } = await fetchPage(roomId);
      setMessages(msgs);
      setHasMore(hasMore);
      setNextBefore(nextBefore);
      await markMessagesAsSeen(roomId, new Date().toISOString());
    } finally { setMessagesLoading(false); }
  }, []);

  const loadOlder = async () => {
    if (!hasMore || loadingOlder || !selectedRoomId) return;
    setLoadingOlder(true);
    try {
      const { msgs, hasMore: hm, nextBefore: nb } =
        await fetchPage(selectedRoomId, nextBefore);
      setMessages(prev => {
        const seen = new Set(prev.map(m => m.id));
        const unique = msgs.filter(m => !seen.has(m.id));
        return [...unique, ...prev];
      });
      setHasMore(hm); setNextBefore(nb);
    } finally { setLoadingOlder(false); }
  };

  const handleSend = async text => {
    if (!selectedRoomId) return;
    await sendMessage(selectedRoomId, text);
    await loadInitialMessages(selectedRoomId);
  };

  /* polling */
  useEffect(() => {
    if (!selectedRoomId) return;
    const poll = async () => {
      const { msgs } = await fetchPage(selectedRoomId);
      setMessages(prev => {
        const last = prev.length ? prev[prev.length - 1].sentAt : null;
        const fresh = msgs.filter(m => !last || new Date(m.sentAt) > new Date(last));
        if (fresh.length === 0) return prev;

        const nearBottom =
          containerRef.current &&
          containerRef.current.scrollHeight -
          containerRef.current.scrollTop -
          containerRef.current.clientHeight < 100;

        const next = [...prev, ...fresh];
        if (nearBottom) {
          setTimeout(() => {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
          }, 50);
        }
        return next;
      });
    };
    pollingRef.current = setInterval(poll, POLL_INTERVAL);
    return () => clearInterval(pollingRef.current);
  }, [selectedRoomId]);

  useEffect(() => { loadRooms(); }, []);
  useEffect(() => { loadInitialMessages(selectedRoomId); },
           [selectedRoomId, loadInitialMessages]);

  const roomsVisible = chatRooms.filter(r =>
    (r.studentFullName || "").toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Box display="flex" flex={1} sx={{ minHeight: 0 }}>
      {/* Rooms */}
      <Box
        width={320} flexShrink={0} borderRight="1px solid #ddd"
        display="flex" flexDirection="column"
      >
        <Stack direction="row" spacing={1} p={1}>
          <TextField
            fullWidth size="small" placeholder="Search…"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
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

      {/* Messages */}
      <Box display="flex" flexDirection="column" flex={1} sx={{ minHeight: 0 }}>
        <ChatMessageList
          messages={messages}
          loading={messagesLoading}
          hasMore={hasMore}
          loadingOlder={loadingOlder}
          loadOlder={loadOlder}
          containerRef={containerRef}
        />
        <Divider />
        <ChatMessageInput disabled={!selectedRoomId} onSend={handleSend} />
      </Box>
    </Box>
  );
};

export default MentorChatPage;
