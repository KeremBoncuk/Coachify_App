import {
  Box,
  Divider,
  TextField,
  Stack,
} from "@mui/material";
import { useEffect, useState, useCallback } from "react";

import ChatRoomList from "./ChatRoomList";
import ChatMessageList from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import {
  getActiveChatRooms,
  getPaginatedMessages,
  markMessagesAsSeen,
} from "../../../api/mentorChat";
import { useWebSocket } from '../../../hooks/useWebSocket';
import { getStudentById } from "../../../api/adminUsers";

const PAGE_SIZE = 20;

const MentorChatPage = () => {
  /* rooms */
  const [chatRooms, setChatRooms] = useState([]);
  const [roomsLoading, setRoomsLoading] = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  /* messages */
  const [messages, setMessages] = useState([]);
  const [hasMore, setHasMore] = useState(false);
  const [nextBefore, setNextBefore] = useState(null);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingOlder, setLoadingOlder] = useState(false);

  /* search */
  const [searchTerm, setSearchTerm] = useState("");

  /* ───── rooms fetch + name enrichment ───── */
  const loadRooms = async () => {
    setRoomsLoading(true);
    try {
      const { data } = await getActiveChatRooms();

      // fill in missing studentFullName
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

  /* ───── page fetch ───── */
  const fetchPage = async (roomId, before = null) => {
    const { data } = await getPaginatedMessages(roomId, before, PAGE_SIZE);
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

        // Mark messages as seen up to the latest message received
        const latestMessageTime = msgs.length > 0 ? msgs[msgs.length - 1].sentAt : new Date().toISOString();
        await markMessagesAsSeen(roomId, latestMessageTime);

        // Update local state to reflect messages as seen by mentor
        setMessages(currentMsgs =>
          currentMsgs.map(msg => ({
            ...msg,
            seenStatus: { ...msg.seenStatus, seenByMentor: true },
          }))
        );
      } finally {
        setMessagesLoading(false);
      }
    },
    []
  );

  /* prepend older */
  const loadOlder = async () => {
    if (!hasMore || loadingOlder || !selectedRoomId) return;
    setLoadingOlder(true);
    try {
      const { msgs, hasMore: hm, nextBefore: nb } = await fetchPage(
        selectedRoomId,
        nextBefore
      );
      setMessages((prev) => {
        const seen = new Set(prev.map((m) => m.id));
        const unique = msgs.filter((m) => !seen.has(m.id));
        return [...unique, ...prev];
      });
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

  const handleSend = async (text) => {
    if (!selectedRoomId) return;
    sendWsMessage("/app/chat.sendMessage", {
      chatRoomId: selectedRoomId,
      text,
      mediaUrls: [],
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
      {/* rooms */}
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

      {/* messages */}
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
