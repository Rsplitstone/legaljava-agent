import { io, Socket } from 'socket.io-client';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useState, useEffect } from 'react';

// Create socket connection conditionally to avoid ERR_CONNECTION_REFUSED
// This allows the app to work even when the socket server isn't running
const socketUrl = import.meta.env.VITE_WS_URL || 'http://localhost:3002';
let socket: Socket | null = null;

try {
  // Only connect in production or if explicitly enabled
  if (import.meta.env.PROD || import.meta.env.VITE_ENABLE_WEBSOCKETS === 'true') {
    socket = io(socketUrl);
    console.log(`Socket.IO connecting to: ${socketUrl}`);
  } else {
    console.log('Socket.IO disabled in development mode');
  }
} catch (err) {
  console.warn('Socket.IO connection failed:', err);
}

export default function ChatPanel() {
  const [messages, setMessages] = useState<string[]>([]);
  const [input, setInput] = useState('');

  // Set up socket event listeners with useEffect to avoid duplicates
  useEffect(() => {
    if (socket) {
      // Handle incoming deadline alerts
      socket.on('deadlineAlert', (msg: string) => toast(msg));
      
      // Handle chat responses
      socket.on('chatResponse', (response: string) => {
        setMessages((prev) => [...prev, `AI: ${response}`]);
      });
      
      // Cleanup on unmount
      return () => {
        if (socket) {
          socket.off('deadlineAlert');
          socket.off('chatResponse');
        }
      };
    }
  }, []);

  const sendMessage = () => {
    if (input.trim()) {
      setMessages((prev) => [...prev, `You: ${input}`]);
      if (socket) {
        socket.emit('chatMessage', input);
      } else {
        // Fallback for when socket is not available
        setMessages((prev) => [...prev, `AI: Sorry, real-time chat is currently unavailable.`]);
      }
      setInput('');
    }
  };

  return (
    <div className="chat-panel">
      <h2>Chat Panel</h2>
      <div className="messages">
        {messages.map((msg, index) => (
          <p key={index}>{msg}</p>
        ))}
      </div>
      <div className="chat-input">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type your message..."
        />
        <button onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}
