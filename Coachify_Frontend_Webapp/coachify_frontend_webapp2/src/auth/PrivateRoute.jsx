import { Navigate, Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import { getRoleFromToken, isTokenExpired, removeToken } from "../auth/jwtUtils";
import { validateToken } from "../api/validateJwtApi";

/**
 * Guards nested routes with both local JWT checks and backend validation.
 *
 * Usage:
 * <Route element={<PrivateRoute allowedRoles={['ADMIN']} />}>
 *   <Route path="/admin/dashboard" element={<AdminDashboard />} />
 * </Route>
 */
const PrivateRoute = ({ allowedRoles }) => {
  const [loading, setLoading] = useState(true);
  const [valid, setValid] = useState(false);

  const role = getRoleFromToken();
  const expired = isTokenExpired();

  useEffect(() => {
    const checkToken = async () => {
      if (!role || expired || !allowedRoles.includes(role)) {
        removeToken();  // ✅ Clear token immediately
        setValid(false);
        setLoading(false);
        return;
      }

      try {
        await validateToken();  // Backend validation (checks revocation too)
        setValid(true);
      } catch (err) {
        console.error("Token validation failed", err);
        removeToken();  // ✅ Clear token if revoked/invalid
        setValid(false);
      } finally {
        setLoading(false);
      }
    };

    checkToken();
  }, [role, expired, allowedRoles]);

  if (loading) return <div>Loading...</div>;  // You can replace with a better spinner if desired

  if (!valid) return <Navigate to="/login" replace />;

  return <Outlet />;
};

export default PrivateRoute;
