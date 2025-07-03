import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
});

// Automatically attach JWT token (if exists)
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
