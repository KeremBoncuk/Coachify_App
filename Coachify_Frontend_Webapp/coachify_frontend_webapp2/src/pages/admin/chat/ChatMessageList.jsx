import {
  List,
  Box,
  Typography,
  CircularProgress,
  Paper,
} from "@mui/material";
import {
  useEffect,
  useState,
  useMemo,
  useRef,
  useLayoutEffect,
} from "react";
import dayjs from "dayjs";
import { formatTime } from "../../../utils/formatTime";
import {
  getMentorById,
  getStudentById,
  getAdminById,
} from "../../../api/adminUsers";
import { getUserIdFromToken } from "../../../auth/jwtUtils";
import { CheckCircle, RadioButtonUnchecked } from "@mui/icons-material";

const Sup = ({ children }) => (
  <sup style={{ fontSize: "0.7em", verticalAlign: "super" }}>{children}</sup>
);
const formatDateHeader = (iso) => {
  const d = dayjs(iso);
  if (dayjs().isSame(d, "day"))           return "Today";
  if (dayjs().subtract(1, "day").isSame(d,"day")) return "Yesterday";
  return d.format("DD MMM YYYY");
};
const SeenIcon = ({ seen }) =>
  seen ? (
    <CheckCircle fontSize="small" sx={{ color: "green" }} />
  ) : (
    <RadioButtonUnchecked fontSize="small" sx={{ color: "grey.500" }} />
  );

const ChatMessageList = ({
  messages,
  loading,
  hasMore,
  loadingOlder,
  loadOlder,
  containerRef,          // NEW
}) => {
  const [nameMap, setNameMap] = useState({});
  const currentUserId = useMemo(() => getUserIdFromToken(), []);

  const innerRef = containerRef || useRef(null);
  const topSentinel = useRef(null);
  const bottomRef   = useRef(null);

  /* fetch names (unchanged) */
  useEffect(() => {
    const toFetch = {};
    messages.forEach((m) => {
      if (!nameMap[m.senderId]) toFetch[m.senderId] = m.senderRole;
    });
    if (Object.keys(toFetch).length === 0) return;
    (async () => {
      try {
        const tasks = Object.entries(toFetch).map(([id, role]) => {
          if (role === "MENTOR")  return getMentorById(id).then(d => ({ id, name: d.fullName }));
          if (role === "STUDENT") return getStudentById(id).then(d => ({ id, name: d.fullName }));
          return getAdminById(id).then(d => ({ id, name: d.fullName }));
        });
        const res = await Promise.all(tasks);
        const add = {};
        res.forEach(({ id, name }) => (add[id] = name));
        setNameMap(prev => ({ ...prev, ...add }));
      } catch { /* ignore */ }
    })();
  }, [messages, nameMap]);

  /* sentinel */
  useEffect(() => {
    if (!topSentinel.current) return;
    const io = new IntersectionObserver(
      ([e]) => e.isIntersecting && loadOlder(),
      { root: innerRef.current, threshold: 0 }
    );
    io.observe(topSentinel.current);
    return () => io.disconnect();
  }, [loadOlder]);

  /* maintain scroll offset */
  const prevHeight = useRef(0);
  const prevLen = useRef(messages.length);
  if (messages.length !== prevLen.current) {
    prevHeight.current = innerRef.current?.scrollHeight || 0;
    prevLen.current = messages.length;
  }
  useLayoutEffect(() => {
    if (loadingOlder && innerRef.current) {
      const diff = innerRef.current.scrollHeight - prevHeight.current;
      innerRef.current.scrollTop = diff;
    }
  }, [loadingOlder]);

  /* scroll to bottom on load */
  useEffect(() => {
    if (!loading && bottomRef.current)
      bottomRef.current.scrollIntoView({ behavior: "auto" });
  }, [loading]);

  const MsgBubble = ({ m }) => {
    const isMine   = m.senderId === currentUserId;
    const sender   = nameMap[m.senderId] || m.senderRole;
    const seenByStudent = !!m.seenStatus?.seenByStudent;
    const seenByMentor  = !!m.seenStatus?.seenByMentor;

    return (
      <Box sx={{ width: "100%", display: "flex", pt: 0.5 }}>
        <Paper
          elevation={0}
          sx={{
            ml: isMine ? "auto" : 0,
            mr: isMine ? 0 : "auto",
            bgcolor: isMine ? "primary.light" : (theme) => theme.palette.mode === 'dark' ? 'grey.700' : 'grey.200',
            p: 1.2,
            borderRadius: 2,
            maxWidth: "75%",
          }}
        >
          <Typography variant="body1" sx={{ whiteSpace: "pre-wrap" }}>
            {m.text}
          </Typography>
          <Box mt={0.5} display="flex" alignItems="center" gap={1}>
            <Typography variant="caption" color="text.secondary">
              {sender} <Sup>({m.senderRole.toLowerCase()})</Sup> · {formatTime(m.sentAt)}
            </Typography>
            <Box display="flex" alignItems="center" gap={0.5}>
              <Typography variant="caption" color="text.secondary">S:</Typography>
              <SeenIcon seen={seenByStudent} />
              <Typography variant="caption" color="text.secondary">M:</Typography>
              <SeenIcon seen={seenByMentor} />
            </Box>
          </Box>
        </Paper>
      </Box>
    );
  };

  return (
    <Box
      ref={innerRef}
      flex={1}
      overflow="auto"
      display="flex"
      flexDirection="column"
      bgcolor="background.default"
      sx={{ minHeight: 0 }}
    >
      {loading ? (
        <Box display="flex" alignItems="center" justifyContent="center" flex={1} p={2}>
          <CircularProgress size={32} />
        </Box>
      ) : messages.length === 0 ? (
        <Typography variant="body2" color="text.secondary" p={2}>
          No messages
        </Typography>
      ) : (
        <List disablePadding sx={{ display: "flex", flexDirection: "column", gap: 1, p: 2 }}>
          <div ref={topSentinel} />
          {loadingOlder && (
            <Box display="flex" justifyContent="center" py={1}>
              <CircularProgress size={20} />
            </Box>
          )}
          {messages.map(m => <MsgBubble key={m.id || m.messageId} m={m} />)}
          <div ref={bottomRef} />
        </List>
      )}
    </Box>
  );
};

export default ChatMessageList;
