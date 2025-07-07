import {
  List,
  ListItemButton,
  ListItemText,
  Divider,
  Typography,
  Box,
  CircularProgress,
} from "@mui/material";

const Sup = ({ children }) => (
  <sup style={{ fontSize: "0.7em", verticalAlign: "super" }}>{children}</sup>
);

/**
 * Props:
 *  • chatRooms [{ id, studentFullName?, studentId }]
 *  • selectedId
 *  • onSelect(id)
 *  • loading
 */
const ChatRoomList = ({ chatRooms, selectedId, onSelect, loading }) => (
  <Box flex={1} overflow="auto">
    {loading ? (
      <Box display="flex" alignItems="center" justifyContent="center" height="100%">
        <CircularProgress size={32} />
      </Box>
    ) : chatRooms.length === 0 ? (
      <Typography variant="body2" color="text.secondary" p={2}>
        No chat rooms
      </Typography>
    ) : (
      <List disablePadding>
        {chatRooms.map((room) => {
          const display =
            room.studentFullName || `#${room.studentId.slice(0, 6)}`;
          return (
            <Box key={room.id}>
              <ListItemButton
                selected={room.id === selectedId}
                onClick={() => onSelect(room.id)}
              >
                <ListItemText
                  primary={
                    <>
                      {display} <Sup>(student)</Sup>
                    </>
                  }
                  primaryTypographyProps={{ noWrap: true }}
                />
              </ListItemButton>
              <Divider />
            </Box>
          );
        })}
      </List>
    )}
  </Box>
);

export default ChatRoomList;
