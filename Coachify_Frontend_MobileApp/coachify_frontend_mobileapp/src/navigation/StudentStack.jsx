import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import StudentWelcomeScreen from '../screens/student/StudentWelcomeScreen';

const Stack = createStackNavigator();

const StudentStack = () => (
  <Stack.Navigator>
    <Stack.Screen
      name="StudentWelcome"
      component={StudentWelcomeScreen}
      options={{ headerShown: false }}
    />
  </Stack.Navigator>
);

export default StudentStack;
