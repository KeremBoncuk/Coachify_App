import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Tooltip,
  Divider,
  Box,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { Menu, Dashboard, People, Chat, School } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const drawerWidth  = 240;
const topbarHeight = 64;

const MentorSidebar = ({ mobileOpen, setMobileOpen }) => {
  const [collapsed, setCollapsed] = useState(true);      // desktop only
  const theme     = useTheme();
  const isMobile  = useMediaQuery(theme.breakpoints.down("md"));
  const navigate  = useNavigate();

  const collapsedNow = isMobile ? false : collapsed;

  const drawerContent = (
    <>
      {!isMobile && (
        <Box display="flex" justifyContent="center" py={1}>
          <IconButton onClick={() => setCollapsed(!collapsed)}>
            <Menu />
          </IconButton>
        </Box>
      )}
      {!isMobile && <Divider />}
      <List>
        <ListItem button onClick={() => navigate("/mentor/dashboard")}>
          <Tooltip title="Dashboard" placement="right" disableHoverListener={!collapsedNow}>
            <ListItemIcon><Dashboard /></ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Dashboard" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/mentor/students")}>
          <Tooltip title="Students" placement="right" disableHoverListener={!collapsedNow}>
            <ListItemIcon><People /></ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Students" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/mentor/chat")}>
          <Tooltip title="Chat" placement="right" disableHoverListener={!collapsedNow}>
            <ListItemIcon><Chat /></ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Chat" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/mentor/study-plans")}>
          <Tooltip title="Study Plans" placement="right" disableHoverListener={!collapsedNow}>
            <ListItemIcon><School /></ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Study Plans" />}
        </ListItem>
      </List>
    </>
  );

  return isMobile ? (
    <Drawer
      variant="temporary"
      open={mobileOpen}
      onClose={() => setMobileOpen(false)}
      ModalProps={{ keepMounted: true }}
      sx={{
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          top: topbarHeight,
          height: `calc(100% - ${topbarHeight}px)`,
        },
      }}
    >
      {drawerContent}
    </Drawer>
  ) : (
    <Drawer
      variant="permanent"
      open
      sx={{
        width: collapsed ? 64 : drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: collapsed ? 64 : drawerWidth,
          top: topbarHeight,
          height: `calc(100% - ${topbarHeight}px)`,
          overflowX: 'hidden',
          transition: 'width 0.3s',
        },
      }}
    >
      {drawerContent}
    </Drawer>
  );
};

export default MentorSidebar;
