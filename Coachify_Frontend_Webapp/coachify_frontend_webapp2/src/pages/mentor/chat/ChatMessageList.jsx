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
}) => {
  const currentUserId = useMemo(() => getUserIdFromToken(), []);

  const containerRef = useRef(null);
  const topSentinel = useRef(null);
  const bottomRef = useRef(null);

  /* infinite-scroll ↑ */
  useEffect(() => {
    if (!topSentinel.current) return;
    const observer = new IntersectionObserver(
      ([entry]) => entry.isIntersecting && loadOlder(),
      { root: containerRef.current, threshold: 0 }
    );
    observer.observe(topSentinel.current);
    return () => observer.disconnect();
  }, [loadOlder]);

  /* maintain scroll offset when prepending */
  const prevHeight = useRef(0);
  const prevLen = useRef(messages.length);
  if (messages.length !== prevLen.current) {
    prevHeight.current = containerRef.current?.scrollHeight || 0;
    prevLen.current = messages.length;
  }
  useLayoutEffect(() => {
    if (loadingOlder && containerRef.current) {
      const diff = containerRef.current.scrollHeight - prevHeight.current;
      containerRef.current.scrollTop = diff;
    }
  }, [loadingOlder]);

  /* scroll to bottom on first load / send */
  useEffect(() => {
    if (!loading && bottomRef.current)
      bottomRef.current.scrollIntoView({ behavior: "auto" });
  }, [loading]);

  /* helpers */
  const DateBubble = ({ label }) => (
    <Box display="flex" justifyContent="center" mt={1} mb={1}>
      <Paper elevation={0} sx={{ bgcolor: "grey.300", px: 1.5, py: 0.2, borderRadius: 2 }}>
        <Typography variant="caption" color="text.secondary">
          {label}
        </Typography>
      </Paper>
    </Box>
  );

  const MsgBubble = ({ m }) => {
    const isMine = m.senderId === currentUserId;
    const bg = isMine ? "primary.light" : "grey.200";
    const meta = (
      <>
        {m.senderFullName} <Sup>({m.senderRole.toLowerCase()})</Sup> · {formatTime(m.sentAt)}
      </>
    );

    return (
      <Box sx={{ width: "100%", display: "flex", pt: 0.5 }}>
        <Paper
          elevation={0}
          sx={{
            ml: isMine ? "auto" : 0,
            mr: isMine ? 0 : "auto",
            bgcolor: bg,
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
              {meta}
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

  /* render */
  return (
    <Box
      ref={containerRef}
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
          {(() => {
            const out = [];
            let lastDate = null;
            messages.forEach((m) => {
              const d = dayjs(m.sentAt).format("YYYY-MM-DD");
              if (d !== lastDate) {
                lastDate = d;
                out.push(
                  <DateBubble key={`d-${d}`} label={formatDateHeader(m.sentAt)} />
                );
              }
              out.push(<MsgBubble key={m.id || m.messageId} m={m} />);
            });
            return out;
          })()}
          <div ref={bottomRef} />
        </List>
      )}
    </Box>
  );
};

export default ChatMessageList;
