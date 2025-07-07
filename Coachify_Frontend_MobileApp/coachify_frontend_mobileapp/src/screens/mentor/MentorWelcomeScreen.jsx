import React, { useContext } from 'react';
import { View, StyleSheet } from 'react-native';
import { Text, Button } from 'react-native-paper';
import { AppContext } from '../../context/AppContext';
import { logout } from '../../auth/logout';

const MentorWelcomeScreen = () => {
  const { setRole } = useContext(AppContext);

  const handleLogout = async () => {
    await logout();
    setRole(null); // Auto-redirect to Login
  };

  return (
    <View style={styles.container}>
      <Text variant="headlineMedium">Welcome, Mentor!</Text>
      <Button
        mode="contained"
        onPress={handleLogout}
        style={styles.button}
      >
        Logout
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    marginTop: 24,
  },
});

export default MentorWelcomeScreen;
