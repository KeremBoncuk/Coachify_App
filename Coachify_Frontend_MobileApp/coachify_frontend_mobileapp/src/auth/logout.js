import AsyncStorage from '@react-native-async-storage/async-storage';
import axiosInstance from '../api/axiosInstance';

/**
 * Logs out the user:
 * - Sends POST to /auth/logout
 * - Clears token
 * - Clears AppContext role (handled by caller)
 */
export const logout = async () => {
  try {
    await axiosInstance.post('/auth/logout');
  } catch (err) {
    console.warn('Logout failed or already expired', err);
  }

  await AsyncStorage.removeItem('token');
};
