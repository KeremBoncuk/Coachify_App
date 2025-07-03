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
import {
  Menu,
  Dashboard,
  Chat,
  School,
  Groups,
  EmojiPeople,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const drawerWidth = 240;
const topbarHeight = 64;

const AdminSidebar = ({ mobileOpen, setMobileOpen }) => {
  const [collapsed, setCollapsed] = useState(true);        // desktop only
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));
  const navigate = useNavigate();

  const collapsedNow = isMobile ? false : collapsed;

  const drawerContent = (
    <>
      {/* Collapse toggle (desktop only) */}
      {!isMobile && (
        <>
          <Box display="flex" justifyContent="center" py={1}>
            <IconButton onClick={() => setCollapsed((c) => !c)}>
              <Menu />
            </IconButton>
          </Box>
          <Divider />
        </>
      )}

      {/* NAV ITEMS */}
      <List>
        <ListItem button onClick={() => navigate("/admin/dashboard")}>
          <Tooltip
            title="Dashboard"
            placement="right"
            disableHoverListener={!collapsedNow}
          >
            <ListItemIcon>
              <Dashboard />
            </ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Dashboard" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/admin/students")}>
          <Tooltip
            title="Students"
            placement="right"
            disableHoverListener={!collapsedNow}
          >
            <ListItemIcon>
              <Groups />
            </ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Students" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/admin/mentors")}>
          <Tooltip
            title="Mentors"
            placement="right"
            disableHoverListener={!collapsedNow}
          >
            <ListItemIcon>
              <EmojiPeople />
            </ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Mentors" />}
        </ListItem>

        <ListItem button onClick={() => navigate("/admin/study-programs")}>
          <Tooltip
            title="Study Programs"
            placement="right"
            disableHoverListener={!collapsedNow}
          >
            <ListItemIcon>
              <School />
            </ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Study Programs" />}
        </ListItem>

        {/* NEW – Chat Monitor */}
        <ListItem button onClick={() => navigate("/admin/chat-monitor")}>
          <Tooltip
            title="Chat Monitor"
            placement="right"
            disableHoverListener={!collapsedNow}
          >
            <ListItemIcon>
              <Chat />
            </ListItemIcon>
          </Tooltip>
          {!collapsedNow && <ListItemText primary="Chat Monitor" />}
        </ListItem>
      </List>
    </>
  );

  /* ===== RENDER ===== */
  return isMobile ? (
    /* Mobile temporary drawer */
    <Drawer
      variant="temporary"
      open={mobileOpen}
      onClose={() => setMobileOpen(false)}
      ModalProps={{ keepMounted: true }}
      sx={{
        "& .MuiDrawer-paper": {
          width: drawerWidth,
          top: topbarHeight,
          height: `calc(100% - ${topbarHeight}px)`,
        },
      }}
    >
      {drawerContent}
    </Drawer>
  ) : (
    /* Desktop permanent drawer */
    <Drawer
      variant="permanent"
      open
      sx={{
        width: collapsed ? 64 : drawerWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: collapsed ? 64 : drawerWidth,
          top: topbarHeight,
          height: `calc(100% - ${topbarHeight}px)`,
          overflowX: "hidden",
          transition: "width 0.3s",
        },
      }}
    >
      {drawerContent}
    </Drawer>
  );
};

export default AdminSidebar;
