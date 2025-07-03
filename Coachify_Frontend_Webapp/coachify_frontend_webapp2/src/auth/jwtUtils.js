import { jwtDecode } from "jwt-decode";  // already installed

/**
 * Retrieves the stored JWT from localStorage.
 */
export const getToken = () => localStorage.getItem("token");

/**
 * Removes the JWT from localStorage.
 */
export const removeToken = () => {
  localStorage.removeItem("token");
};

/**
 * Extracts the user's role from the JWT.
 * Returns null if token is missing or invalid.
 */
export const getRoleFromToken = () => {
  const token = getToken();
  if (!token) return null;
  try {
    return jwtDecode(token).role;
  } catch {
    return null;
  }
};

/**
 * Checks if the JWT is expired.
 * Returns true if missing, invalid, or expired.
 */
export const isTokenExpired = () => {
  const token = getToken();
  if (!token) return true;
  try {
    const { exp } = jwtDecode(token);
    return Date.now() >= exp * 1000;
  } catch {
    return true;
  }
};

/**
 * Extracts userId (ObjectId) from JWT's 'sub' field.
 * Returns null if token missing or invalid.
 */
export const getUserIdFromToken = () => {
  const token = getToken();
  if (!token) return null;
  try {
    return jwtDecode(token).sub;
  } catch {
    return null;
  }
};
