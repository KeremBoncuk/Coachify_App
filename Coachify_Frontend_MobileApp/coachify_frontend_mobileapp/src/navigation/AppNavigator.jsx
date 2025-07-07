import React, { useContext } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import AuthStack from './AuthStack';
import StudentStack from './StudentStack';
import MentorStack from './MentorStack';
import { AppContext } from '../context/AppContext';
import { ActivityIndicator, View } from 'react-native';

const AppNavigator = () => {
  const { role, isLoading } = useContext(AppContext);

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      {role === 'STUDENT' ? (
        <StudentStack />
      ) : role === 'MENTOR' ? (
        <MentorStack />
      ) : (
        <AuthStack />
      )}
    </NavigationContainer>
  );
};

export default AppNavigator;
