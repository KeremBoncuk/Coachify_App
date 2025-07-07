import React, { createContext, useState, useEffect } from 'react';
import { validateToken } from '../api/validateJwtApi';
import { getRoleFromToken, isTokenExpired } from '../auth/jwtUtils';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [role, setRole] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const checkAuth = async () => {
    try {
      const expired = await isTokenExpired();
      if (expired) throw new Error('Token expired');

      await validateToken();
      const userRole = await getRoleFromToken();
      setRole(userRole);
    } catch (err) {
      setRole(null);
      await AsyncStorage.removeItem('token');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    checkAuth();
  }, []);

  return (
    <AppContext.Provider value={{ role, setRole, isLoading, checkAuth }}>
      {children}
    </AppContext.Provider>
  );
};
