import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import cors from 'cors';

const app = express();
const httpServer = createServer(app);

// Configure CORS for both Express and Socket.io
app.use(cors({
  origin: ["http://localhost:5173", "http://localhost:5174", "http://localhost:3000"],
  credentials: true
}));

const io = new Server(httpServer, {
  cors: {
    origin: ["http://localhost:5173", "http://localhost:5174", "http://localhost:3000"],
    methods: ["GET", "POST"],
    credentials: true
  }
});

// Middleware for parsing JSON
app.use(express.json());

// Basic health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'WebSocket server is running', timestamp: new Date().toISOString() });
});

// Socket.io connection handling
io.on('connection', (socket) => {
  console.log('User connected:', socket.id);

  // Handle chat messages
  socket.on('chatMessage', (message) => {
    console.log('Received chat message:', message);
    
    // Simple AI response simulation
    setTimeout(() => {
      const responses = [
        "I understand your legal question. Let me help you with that.",
        "Based on the legal documents, I can provide some insights.",
        "That's an interesting legal matter. Let me analyze it for you.",
        "I'll review the relevant legal precedents for your case.",
        "This question relates to several legal areas. Let me explain."
      ];
      
      const randomResponse = responses[Math.floor(Math.random() * responses.length)];
      socket.emit('chatResponse', randomResponse);
    }, 1000);
  });

  // Handle task updates for real-time Kanban updates
  socket.on('taskUpdate', (taskData) => {
    console.log('Task updated:', taskData);
    // Broadcast to all connected clients except sender
    socket.broadcast.emit('taskUpdated', taskData);
  });

  // Handle deadline alerts
  socket.on('setDeadlineReminder', (deadlineData) => {
    console.log('Deadline reminder set:', deadlineData);
    
    // Simulate deadline alert after 30 seconds for demo
    setTimeout(() => {
      socket.emit('deadlineAlert', `Reminder: ${deadlineData.title} is approaching!`);
    }, 30000);
  });

  // Handle file upload notifications
  socket.on('fileUploadStarted', (fileData) => {
    console.log('File upload started:', fileData);
    socket.emit('uploadProgress', { filename: fileData.name, progress: 0 });
    
    // Simulate upload progress
    let progress = 0;
    const progressInterval = setInterval(() => {
      progress += 10;
      socket.emit('uploadProgress', { filename: fileData.name, progress });
      
      if (progress >= 100) {
        clearInterval(progressInterval);
        socket.emit('uploadComplete', { filename: fileData.name, message: 'File uploaded successfully!' });
      }
    }, 500);
  });

  // Handle system notifications
  socket.on('systemNotification', (notification) => {
    console.log('System notification:', notification);
    io.emit('notification', notification); // Broadcast to all clients
  });

  // Handle disconnection
  socket.on('disconnect', () => {
    console.log('User disconnected:', socket.id);
  });
});

// Send periodic system updates
setInterval(() => {
  io.emit('systemStatus', {
    timestamp: new Date().toISOString(),
    connectedUsers: io.sockets.sockets.size,
    message: 'System is running normally'
  });
}, 60000); // Every minute

const PORT = process.env.WS_PORT || 3001;

httpServer.listen(PORT, () => {
  console.log(`🚀 WebSocket server running on port ${PORT}`);
  console.log(`📡 Socket.io enabled with CORS for frontend`);
  console.log(`🔗 Health check available at http://localhost:${PORT}/health`);
});

export { app, io };
