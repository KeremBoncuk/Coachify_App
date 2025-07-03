import { useState } from "react";
import { getRoleFromToken } from "../auth/jwtUtils";
import AdminTopbar from "./topbar/AdminTopbar";
import MentorTopbar from "./topbar/MentorTopbar";
import AdminSidebar from "./sidebar/AdminSidebar";
import MentorSidebar from "./sidebar/MentorSidebar";
import { Outlet } from "react-router-dom";
import { Box, Toolbar } from "@mui/material";

const TOPBAR_HEIGHT = 64;               // AppBar height

const Layout = () => {
  const role = getRoleFromToken();
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <Box sx={{ height: "100vh", display: "flex", flexDirection: "column" }}>
      {/* ─── TOPBAR ─────────────────────────────────────── */}
      {role === "ADMIN" && (
        <AdminTopbar onMenuClick={() => setMobileOpen(true)} />
      )}
      {role === "MENTOR" && (
        <MentorTopbar onMenuClick={() => setMobileOpen(true)} />
      )}

      {/* ─── SIDEBAR + MAIN CONTENT ────────────────────── */}
      <Box sx={{ display: "flex", flexGrow: 1, minHeight: 0 }}>
        {role === "ADMIN" && (
          <AdminSidebar mobileOpen={mobileOpen} setMobileOpen={setMobileOpen} />
        )}
        {role === "MENTOR" && (
          <MentorSidebar
            mobileOpen={mobileOpen}
            setMobileOpen={setMobileOpen}
          />
        )}

        {/* MAIN */}
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            display: "flex",
            flexDirection: "column",
            p: 2,               /* smaller, uniform padding */
            overflow: "hidden", /* prevent outer scrollbars */
            minHeight: 0,       /* let children shrink/scroll */
          }}
        >
          {/* pushes content below the fixed AppBar */}
          <Toolbar sx={{ minHeight: TOPBAR_HEIGHT }} />

          {/* routed page */}
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
};

export default Layout;
