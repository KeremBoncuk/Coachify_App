import { Routes, Route } from "react-router-dom";

import LoginPage from "../pages/LoginPage";
import PrivateRoute from "../auth/PrivateRoute";
import Layout from "../components/Layout";

/* Admin pages */
import AdminDashboard from "../pages/admin/Dashboard";
import AdminStudentsPage from "../pages/admin/students/AdminStudentsPage";
import AdminMentorsPage from "../pages/admin/mentors/AdminMentorsPage";
import ChatMonitorPage from "../pages/admin/chat/ChatMonitorPage";

/* Mentor pages */
import MentorDashboard from "../pages/mentor/Dashboard";
import MentorChatPage from "../pages/mentor/chat/MentorChatPage";   // ← NEW

const AppRoutes = () => (
  <Routes>
    {/* Public */}
    <Route path="/login" element={<LoginPage />} />

    {/* ADMIN */}
    <Route element={<PrivateRoute allowedRoles={["ADMIN"]} />}>
      <Route element={<Layout />}>
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/students" element={<AdminStudentsPage />} />
        <Route path="/admin/mentors" element={<AdminMentorsPage />} />
        <Route path="/admin/chat-monitor" element={<ChatMonitorPage />} />
      </Route>
    </Route>

    {/* MENTOR */}
    <Route element={<PrivateRoute allowedRoles={["MENTOR"]} />}>
      <Route element={<Layout />}>
        <Route path="/mentor/dashboard" element={<MentorDashboard />} />
        <Route path="/mentor/chat" element={<MentorChatPage />} /> {/* NEW */}
      </Route>
    </Route>

    {/* Fallback */}
    <Route path="*" element={<LoginPage />} />
  </Routes>
);

export default AppRoutes;
