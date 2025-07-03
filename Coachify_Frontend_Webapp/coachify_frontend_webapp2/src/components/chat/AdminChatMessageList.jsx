import {
  List,
  Box,
  Typography,
  CircularProgress,
  Paper,
} from "@mui/material";
import { useEffect, useState, useMemo, useRef } from "react";
import { formatTime } from "../../utils/formatTime";
import {
  getMentorById,
  getStudentById,
  getAdminById,
} from "../../api/adminUsers";
import { getUserIdFromToken } from "../../auth/jwtUtils";

const Sup = ({ children }) => (
  <sup style={{ fontSize: "0.7em", verticalAlign: "super" }}>{children}</sup>
);

/**
 * AdminChatMessageList
 * --------------------
 * Props:
 *  • messages : array
 *  • loading  : boolean
 */
const AdminChatMessageList = ({ messages, loading }) => {
  /* cache senderId → fullName */
  const [nameMap, setNameMap] = useState({});

  /* logged-in admin’s ObjectId */
  const currentUserId = useMemo(() => getUserIdFromToken(), []);

  /* auto-scroll ref */
  const bottomRef = useRef(null);

  /* ───── fetch missing sender names ───── */
  useEffect(() => {
    const idsToFetch = {};
    messages.forEach((m) => {
      if (!nameMap[m.senderId]) idsToFetch[m.senderId] = m.senderRole;
    });
    if (Object.keys(idsToFetch).length === 0) return;

    (async () => {
      try {
        const requests = Object.entries(idsToFetch).map(([id, role]) => {
          if (role === "MENTOR")
            return getMentorById(id).then((d) => ({ id, name: d.fullName }));
          if (role === "STUDENT")
            return getStudentById(id).then((d) => ({ id, name: d.fullName }));
          return getAdminById(id).then((d) => ({ id, name: d.fullName }));
        });
        const results = await Promise.all(requests);
        const newEntries = {};
        results.forEach(({ id, name }) => (newEntries[id] = name));
        setNameMap((prev) => ({ ...prev, ...newEntries }));
      } catch (err) {
        console.error("Name lookup failed:", err);
      }
    })();
  }, [messages, nameMap]);

  /* ───── auto-scroll to newest ───── */
  useEffect(() => {
    if (bottomRef.current) bottomRef.current.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  /* ───── RENDER ───── */
  return (
    <Box
      flex={1}
      overflow="auto"
      display="flex"
      flexDirection="column"
      p={2}
      bgcolor="background.default"
      sx={{ minHeight: 0 }}   /* makes flexbox scrolling reliable */
    >
      {loading ? (
        <Box display="flex" alignItems="center" justifyContent="center" flex={1}>
          <CircularProgress size={32} />
        </Box>
      ) : messages.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No messages
        </Typography>
      ) : (
        <List disablePadding sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
          {messages.map((m) => {
            const isMine   = m.senderId === currentUserId;
            const bubbleBg = isMine ? "primary.light" : "grey.200";

            const senderName = nameMap[m.senderId] || m.senderRole;
            const meta = (
              <>
                {senderName} <Sup>({m.senderRole.toLowerCase()})</Sup> ·{" "}
                {formatTime(m.sentAt)}
              </>
            );

            return (
              <Box
                key={m.id}
                sx={{
                  width: "100%",            /* full row width        */
                  display: "flex",
                  pt: 0.5,
                }}
              >
                <Paper
                  elevation={0}
                  sx={{
                    ml: isMine ? "auto" : 0,   /* push mine right       */
                    mr: isMine ? 0 : "auto",   /* push others left      */
                    bgcolor: bubbleBg,
                    p: 1.2,
                    borderRadius: 2,
                    maxWidth: "75%",
                  }}
                >
                  <Typography variant="body1" sx={{ whiteSpace: "pre-wrap" }}>
                    {m.text}
                  </Typography>
                  <Typography
                    variant="caption"
                    component="div"
                    color="text.secondary"
                    mt={0.5}
                  >
                    {meta}
                  </Typography>
                </Paper>
              </Box>
            );
          })}
          <div ref={bottomRef} />
        </List>
      )}
    </Box>
  );
};

export default AdminChatMessageList;
