import axios from './axiosInstance';

export const login = (payload) =>
  axios.post('/auth/login', payload);
