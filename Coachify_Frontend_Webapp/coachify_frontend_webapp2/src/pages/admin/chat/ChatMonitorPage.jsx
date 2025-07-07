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

import ChatRoomList     from "./ChatRoomList";
import ChatMessageList  from "./ChatMessageList";
import ChatMessageInput from "./ChatMessageInput";

import {
  getChatRooms,
  getMessagesPage,
  sendMessage,
} from "../../../api/adminChat";

const PAGE_SIZE     = 20;
const POLL_INTERVAL = 2000; // ms

const ChatMonitorPage = () => {
  /* rooms */
  const [chatRooms, setChatRooms] = useState([]);
  const [roomsLoading, setRoomsLoading] = useState(true);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  /* messages */
  const [messages, setMessages] = useState([]);
  const [hasMore, setHasMore]   = useState(false);
  const [nextBefore, setNextBefore] = useState(null);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingOlder, setLoadingOlder]       = useState(false);

  /* filters */
  const [searchTerm,  setSearchTerm]  = useState("");
  const [statusFilter, setStatusFilter] = useState("ACTIVE");

  const containerRef = useRef(null);
  const pollingRef   = useRef(null);

  /* load rooms */
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

  /* fetch one page (oldest→newest) */
  const fetchPage = async (roomId, before = null) => {
    const { data } = await getMessagesPage(roomId, before, PAGE_SIZE);
    return {
      msgs      : data.messages.slice().reverse(),
      hasMore   : data.hasMore,
      nextBefore: data.nextBefore,
    };
  };

  /* first load */
  const loadInitialMessages = useCallback(async (roomId) => {
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
    } finally { setMessagesLoading(false); }
  }, []);

  /* prepend older */
  const loadOlder = async () => {
    if (!hasMore || loadingOlder || !selectedRoomId) return;
    setLoadingOlder(true);
    try {
      const { msgs, hasMore: hm, nextBefore: nb } =
        await fetchPage(selectedRoomId, nextBefore);
      setMessages(prev => [...msgs, ...prev]);
      setHasMore(hm);
      setNextBefore(nb);
    } finally { setLoadingOlder(false); }
  };

  /* send */
  const handleSend = async (text) => {
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

  /* effects */
  useEffect(() => { loadRooms(); }, [statusFilter]);
  useEffect(() => { loadInitialMessages(selectedRoomId); },
           [selectedRoomId, loadInitialMessages]);

  /* filter rooms */
  const visibleRooms = chatRooms.filter(r =>
    `${r.mentorFullName} ${r.studentFullName}`.toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  /* render */
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
          <FormControl size="small" sx={{ minWidth: 110 }}>
            <Select
              value={statusFilter}
              onChange={e => setStatusFilter(e.target.value)}
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

export default ChatMonitorPage;
