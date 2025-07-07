import React, { useState, useContext } from 'react';
import {
  View,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import {
  TextInput,
  Button,
  Text,
  RadioButton,
} from 'react-native-paper';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { login } from '../api/auth';
import { jwtDecode } from 'jwt-decode';
import { AppContext } from '../context/AppContext';

const LoginScreen = () => {
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('STUDENT'); // Default role
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { setRole: setAppRole } = useContext(AppContext);

  const handleLogin = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await login({ fullName, password, role });
      const token = response.data.token;

      await AsyncStorage.setItem('token', token);

      const decoded = jwtDecode(token);
      setAppRole(decoded.role);  // Auto-reroute via AppContext + AppNavigator
    } catch (err) {
      console.warn(err);
      setError('Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <Text variant="headlineLarge" style={styles.header}>
        Login
      </Text>

      <TextInput
        label="Full Name"
        value={fullName}
        onChangeText={setFullName}
        style={styles.input}
        autoCapitalize="words"
      />
      <TextInput
        label="Password"
        value={password}
        onChangeText={setPassword}
        style={styles.input}
        secureTextEntry
      />

      <RadioButton.Group
        onValueChange={setRole}
        value={role}
      >
        <View style={styles.radioRow}>
          <RadioButton value="STUDENT" />
          <Text>Student</Text>
          <RadioButton value="MENTOR" />
          <Text>Mentor</Text>
        </View>
      </RadioButton.Group>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Button
        mode="contained"
        onPress={handleLogin}
        loading={loading}
        disabled={!fullName || !password}
        style={styles.button}
      >
        Login
      </Button>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: 'center',
  },
  header: {
    marginBottom: 24,
    textAlign: 'center',
  },
  input: {
    marginBottom: 16,
  },
  button: {
    marginTop: 16,
  },
  radioRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  error: {
    color: 'red',
    textAlign: 'center',
  },
});

export default LoginScreen;
