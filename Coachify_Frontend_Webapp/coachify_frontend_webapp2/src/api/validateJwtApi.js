// src/api/validateJwtApi.js
import axios from './axiosInstance';

/**
 * Calls the backend to check if the current JWT is valid and active in the database.
 * Returns a promise that resolves if the token is valid, rejects otherwise.
 */
export const validateToken = () => {
  return axios.get('/auth/validate-token');
};
