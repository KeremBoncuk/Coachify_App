import axios from './axiosInstance';

/**
 * Calls the backend to check if the current JWT is valid and active in the database.
 */
export const validateToken = () => {
  return axios.get('/auth/validate-token');
};
