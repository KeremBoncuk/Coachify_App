import AsyncStorage from '@react-native-async-storage/async-storage';
import { jwtDecode } from 'jwt-decode';

/**
 * Retrieves the stored JWT from AsyncStorage.
 */
export const getToken = async () => {
  return await AsyncStorage.getItem('token');
};

/**
 * Removes the JWT from AsyncStorage.
 */
export const removeToken = async () => {
  await AsyncStorage.removeItem('token');
};

/**
 * Extracts the user's role from the JWT.
 */
export const getRoleFromToken = async () => {
  const token = await getToken();
  if (!token) return null;
  try {
    return jwtDecode(token).role;
  } catch {
    return null;
  }
};

/**
 * Checks if the JWT is expired.
 */
export const isTokenExpired = async () => {
  const token = await getToken();
  if (!token) return true;
  try {
    const { exp } = jwtDecode(token);
    return Date.now() >= exp * 1000;
  } catch {
    return true;
  }
};

/**
 * Extracts userId from JWT.
 */
export const getUserIdFromToken = async () => {
  const token = await getToken();
  if (!token) return null;
  try {
    return jwtDecode(token).sub;
  } catch {
    return null;
  }
};
