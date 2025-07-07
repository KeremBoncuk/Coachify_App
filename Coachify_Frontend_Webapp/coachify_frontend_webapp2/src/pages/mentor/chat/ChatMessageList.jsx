import {
  List,
  Box,
  Typography,
  CircularProgress,
  Paper,
  Tooltip,
} from "@mui/material";
import { DoneAll, Done } from "@mui/icons-material";
import {
  useEffect,
  useMemo,
  useRef,
  useLayoutEffect,
} from "react";
import dayjs from "dayjs";
import { formatTime } from "../../../utils/formatTime";
import { getUserIdFromToken } from "../../../auth/jwtUtils";

const Sup = ({ children }) => (
  <sup style={{ fontSize: "0.7em", verticalAlign: "super" }}>{children}</sup>
);
const formatDateHeader = (iso) => {
  const d = dayjs(iso);
  if (dayjs().isSame(d, "day")) return "Today";
  if (dayjs().subtract(1, "day").isSame(d, "day")) return "Yesterday";
  return d.format("DD MMM YYYY");
};

const ChatMessageList = ({
  messages,
  loading,
  hasMore,
  loadingOlder,
  loadOlder,
  containerRef,          // NEW
}) => {
  const currentUserId = useMemo(() => getUserIdFromToken(), []);

  const innerRef     = containerRef || useRef(null);
  const topSentinel  = useRef(null);
  const bottomRef    = useRef(null);

  /* sentinel */
  useEffect(() => {
    if (!topSentinel.current) return;
    const observer = new IntersectionObserver(
      ([entry]) => entry.isIntersecting && loadOlder(),
      { root: innerRef.current, threshold: 0 }
    );
    observer.observe(topSentinel.current);
    return () => observer.disconnect();
  }, [loadOlder]);

  /* keep scroll offset */
  const prevHeight = useRef(0);
  const prevLen = useRef(messages.length);
  if (messages.length !== prevLen.current) {
    prevHeight.current = innerRef.current?.scrollHeight || 0;
    prevLen.current    = messages.length;
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
    const isMine = m.senderId === currentUserId;
    return (
      <Box sx={{ width: "100%", display: "flex", pt: 0.5 }}>
        <Paper
          elevation={0}
          sx={{
            ml: isMine ? "auto" : 0,
            mr: isMine ? 0 : "auto",
            bgcolor: isMine ? "primary.light" : "grey.200",
            p: 1.2,
            borderRadius: 2,
            maxWidth: "75%",
          }}
        >
          <Typography variant="body1" sx={{ whiteSpace: "pre-wrap" }}>
            {m.text}
          </Typography>
          <Box display="flex" alignItems="center" justifyContent="space-between" mt={0.5}>
            <Typography variant="caption" color="text.secondary">
              {m.senderFullName} <Sup>({m.senderRole.toLowerCase()})</Sup> · {formatTime(m.sentAt)}
            </Typography>
            <Tooltip title={m.seenStatus?.seenByStudent ? "Seen by student" : "Unseen by student"}>
              {m.seenStatus?.seenByStudent ? (
                <DoneAll fontSize="small" color="primary" />
              ) : (
                <Done fontSize="small" color="disabled" />
              )}
            </Tooltip>
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
