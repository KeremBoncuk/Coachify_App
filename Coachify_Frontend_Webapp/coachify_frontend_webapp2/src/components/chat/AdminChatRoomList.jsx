import {
  List,
  ListItemButton,
  ListItemText,
  Divider,
  Typography,
  Box,
  CircularProgress,
} from "@mui/material";

/* tiny helper for superscript styling */
const Sup = ({ children }) => (
  <sup style={{ fontSize: "0.7em", verticalAlign: "super" }}>{children}</sup>
);

/**
 * AdminChatRoomList
 * -----------------
 * Props:
 *  • chatRooms  [{id, mentorFullName, studentFullName, isActive}]
 *  • selectedId
 *  • onSelect(id)
 *  • loading
 */
const AdminChatRoomList = ({ chatRooms, selectedId, onSelect, loading }) => (
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
        {chatRooms.map((room) => (
          <Box key={room.id}>
            <ListItemButton
              selected={room.id === selectedId}
              onClick={() => onSelect(room.id)}
            >
              <ListItemText
                primary={
                  <>
                    {room.mentorFullName}
                    <Sup>(mentor)</Sup> &nbsp;↔&nbsp;
                    {room.studentFullName}
                    <Sup>(student)</Sup>
                  </>
                }
                secondary={room.isActive ? "Active" : "Inactive"}
                primaryTypographyProps={{ noWrap: true }}
              />
            </ListItemButton>
            <Divider />
          </Box>
        ))}
      </List>
    )}
  </Box>
);

export default AdminChatRoomList;
