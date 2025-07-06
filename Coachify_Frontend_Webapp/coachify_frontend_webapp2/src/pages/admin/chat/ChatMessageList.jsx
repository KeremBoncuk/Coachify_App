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

/* ───────────────────────── helpers ───────────────────────── */
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

/* ─────────────────────── component ─────────────────────── */
const ChatMessageList = ({
  messages,
  loading,
  hasMore,
  loadingOlder,
  loadOlder,
}) => {
  /* id → fullName cache */
  const [nameMap, setNameMap] = useState({});
  const currentUserId = useMemo(() => getUserIdFromToken(), []);

  /* refs for scroll handling */
  const containerRef = useRef(null);
  const topSentinel  = useRef(null);
  const bottomRef    = useRef(null);

  /* ── fetch missing names exactly once per unknown id ── */
  useEffect(() => {
    const toFetch = {};
    messages.forEach((m) => {
      if (!nameMap[m.senderId]) toFetch[m.senderId] = m.senderRole;
    });
    if (Object.keys(toFetch).length === 0) return;

    (async () => {
      try {
        const tasks = Object.entries(toFetch).map(([id, role]) => {
          if (role === "MENTOR")  return getMentorById(id).then((d) => ({ id, name: d.fullName }));
          if (role === "STUDENT") return getStudentById(id).then((d) => ({ id, name: d.fullName }));
          return getAdminById(id).then((d) => ({ id, name: d.fullName }));
        });
        const resolved = await Promise.all(tasks);
        const newMap = {};
        resolved.forEach(({ id, name }) => (newMap[id] = name));
        setNameMap((prev) => ({ ...prev, ...newMap }));
      } catch (err) {
        console.error("Name lookup failed:", err);
      }
    })();
  }, [messages, nameMap]);

  /* ── load older messages when the sentinel hits top ── */
  useEffect(() => {
    if (!topSentinel.current) return;
    const io = new IntersectionObserver(
      ([e]) => e.isIntersecting && loadOlder(),
      { root: containerRef.current, threshold: 0 }
    );
    io.observe(topSentinel.current);
    return () => io.disconnect();
  }, [loadOlder]);

  /* ── keep scroll position while prepending ── */
  const prevHeight = useRef(0);
  const prevLen    = useRef(messages.length);
  if (messages.length !== prevLen.current) {
    prevHeight.current = containerRef.current?.scrollHeight || 0;
    prevLen.current    = messages.length;
  }
  useLayoutEffect(() => {
    if (loadingOlder && containerRef.current) {
      const diff = containerRef.current.scrollHeight - prevHeight.current;
      containerRef.current.scrollTop = diff;
    }
  }, [loadingOlder]);

  /* ── scroll to bottom after first load / sending ── */
  useEffect(() => {
    if (!loading && bottomRef.current)
      bottomRef.current.scrollIntoView({ behavior: "auto" });
  }, [loading]);

  /* ── sub-components ── */
  const DateBubble = ({ label }) => (
    <Box display="flex" justifyContent="center" my={1}>
      <Paper
        elevation={0}
        sx={{ bgcolor: "grey.300", px: 1.5, py: 0.2, borderRadius: 2 }}
      >
        <Typography variant="caption" color="text.secondary">
          {label}
        </Typography>
      </Paper>
    </Box>
  );

  const MsgBubble = ({ m }) => {
    const isMine   = m.senderId === currentUserId;
    const bubbleBg = isMine ? "primary.light" : "grey.200";
    const sender   = nameMap[m.senderId] || m.senderRole;

    /* FORCE boolean with “!!” so strings like "true" work */
    const seenByStudent = !!m.seenStatus?.seenByStudent;
    const seenByMentor  = !!m.seenStatus?.seenByMentor;

    return (
      <Box sx={{ width: "100%", display: "flex", pt: 0.5 }}>
        <Paper
          elevation={0}
          sx={{
            ml: isMine ? "auto" : 0,
            mr: isMine ? 0 : "auto",
            bgcolor: bubbleBg,
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
              <Typography variant="caption" color="text.secondary">
                S:
              </Typography>
              <SeenIcon seen={seenByStudent} />
              <Typography variant="caption" color="text.secondary">
                M:
              </Typography>
              <SeenIcon seen={seenByMentor} />
            </Box>
          </Box>
        </Paper>
      </Box>
    );
  };

  /* ── main render ── */
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
              const ymd = dayjs(m.sentAt).format("YYYY-MM-DD");
              if (ymd !== lastDate) {
                lastDate = ymd;
                out.push(
                  <DateBubble key={`date-${ymd}`} label={formatDateHeader(m.sentAt)} />
                );
              }
              out.push(<MsgBubble key={m.messageId ?? m.id} m={m} />);
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
