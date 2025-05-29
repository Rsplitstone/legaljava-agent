import { io } from 'socket.io-client';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useState } from 'react';

const socket = io(import.meta.env.VITE_WS_URL);

socket.on('deadlineAlert', (msg) => toast(msg));

export default function ChatPanel() {
  const [messages, setMessages] = useState<string[]>([]);
  const [input, setInput] = useState('');

  const sendMessage = () => {
    if (input.trim()) {
      setMessages((prev) => [...prev, `You: ${input}`]);
      socket.emit('chatMessage', input);
      setInput('');
    }
  };

  socket.on('chatResponse', (response) => {
    setMessages((prev) => [...prev, `AI: ${response}`]);
  });

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
