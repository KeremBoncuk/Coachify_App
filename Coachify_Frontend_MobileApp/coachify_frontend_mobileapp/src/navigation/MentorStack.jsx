import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import MentorWelcomeScreen from '../screens/mentor/MentorWelcomeScreen';

const Stack = createStackNavigator();

const MentorStack = () => (
  <Stack.Navigator>
    <Stack.Screen
      name="MentorWelcome"
      component={MentorWelcomeScreen}
      options={{ headerShown: false }}
    />
  </Stack.Navigator>
);

export default MentorStack;
