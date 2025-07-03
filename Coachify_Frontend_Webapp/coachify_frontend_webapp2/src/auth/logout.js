import axiosInstance from "../api/axiosInstance";

/**
 * Logs out the user:
 * - Sends POST to /auth/logout
 * - Clears token
 * - Redirects to /login
 */
export const logout = async (navigate) => {
  try {
    await axiosInstance.post("/auth/logout");
  } catch (err) {
    console.warn("Logout failed or already expired", err);
  }

  localStorage.removeItem("token");
  navigate("/login");
};
