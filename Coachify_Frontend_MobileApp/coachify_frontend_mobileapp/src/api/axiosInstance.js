import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';  // ✅ Expo-safe way

const BASE_URL = Constants.expoConfig.extra.apiUrl;  // ✅ Correct way in Expo
console.log('BASE_URL:', BASE_URL);  // Debugging, optional

const axiosInstance = axios.create({
  baseURL: BASE_URL,
});

axiosInstance.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
