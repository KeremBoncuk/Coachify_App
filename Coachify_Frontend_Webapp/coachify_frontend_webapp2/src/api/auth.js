import axios from "axios";

/**
 * Base URL comes from .env  →  REACT_APP_API_URL
 * Fallbacks to localhost for dev convenience.
 */
const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

/**
 * POST /auth/login
 * @param {Object} payload { fullName, password, role }
 * @returns AxiosResponse with { token }
 */
export const login = (payload) => axios.post(`${BASE_URL}/auth/login`, payload);
