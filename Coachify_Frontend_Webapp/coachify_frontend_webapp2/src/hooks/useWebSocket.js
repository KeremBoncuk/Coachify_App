import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { getToken } from '../auth/jwtUtils';

const WS_URL = process.env.REACT_APP_WS_URL || 'http://localhost:8080/ws';

/**
 * Custom hook for WebSocket communication using STOMP.
 * @param {string} topic - The STOMP topic to subscribe to (e.g., '/topic/chat/roomId').
 * @param {function} onMessageReceived - Callback function for incoming messages.
 * @returns {object} - Contains sendMessage function and connection status.
 */
export const useWebSocket = (topic, onMessageReceived) => {
  const [isConnected, setIsConnected] = useState(false);
  const clientRef = useRef(null);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      console.warn('No JWT token found. WebSocket connection not established.');
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log(new Date(), str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket Connected');
        setIsConnected(true);
        client.subscribe(topic, (message) => {
          onMessageReceived(JSON.parse(message.body));
        });
      },
      onDisconnect: () => {
        console.log('WebSocket Disconnected');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [topic, onMessageReceived]);

  const sendMessage = (destination, body) => {
    if (clientRef.current && isConnected) {
      clientRef.current.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.warn('WebSocket not connected. Message not sent.');
    }
  };

  return { sendMessage, isConnected };
};
