import { useState, useRef, useEffect } from 'react';
import { legalService } from '../services/api';
import { Message, QueryRequest } from '../types';

const ChatInterface = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(() => crypto.randomUUID());
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(scrollToBottom, [messages]);

  const addMessage = (text: string, sender: 'user' | 'assistant', citations?: string[]) => {
    const newMessage: Message = {
      id: crypto.randomUUID(),
      text,
      sender,
      timestamp: new Date(),
      citations,
    };
    setMessages(prev => [...prev, newMessage]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim() || isLoading) return;

    const userQuery = inputValue.trim();
    setInputValue('');
    addMessage(userQuery, 'user');
    setIsLoading(true);

    try {
      const request: QueryRequest = {
        query: userQuery,
        sessionId,
        userId: 'demo-user', // In a real app, this would come from authentication
      };

      const response = await legalService.processQuery(request);
      addMessage(response.response, 'assistant', response.citations);
    } catch (error) {
      console.error('Error processing query:', error);
      addMessage('Sorry, I encountered an error processing your request. Please try again.', 'assistant');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden">
      <div className="p-4 bg-gray-50 border-b">
        <h2 className="text-xl font-semibold text-gray-800">Legal Assistant Chat</h2>
        <p className="text-sm text-gray-600 mt-1">Ask me any legal questions and I'll provide detailed answers with citations.</p>
      </div>

      <div className="chat-container h-96 p-4 overflow-y-auto">
        {messages.length === 0 && (
          <div className="text-center text-gray-500 mt-8">
            <p>Welcome! Ask me any legal question to get started.</p>
            <p className="text-sm mt-2">For example: "What are the key elements of a contract?"</p>
          </div>
        )}

        {messages.map((message) => (
          <div
            key={message.id}
            className={`message ${
              message.sender === 'user' ? 'message-user' : 'message-assistant'
            }`}
          >
            <div className="text-sm opacity-75 mb-1">
              {message.sender === 'user' ? 'You' : 'Legal Assistant'}
            </div>
            <div className="whitespace-pre-wrap">{message.text}</div>
            {message.citations && message.citations.length > 0 && (
              <div className="mt-3 pt-3 border-t border-gray-300">
                <div className="text-sm font-medium mb-2">Citations:</div>
                <ul className="text-sm space-y-1">
                  {message.citations.map((citation, index) => (
                    <li key={index} className="text-blue-600">
                      • {citation}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ))}

        {isLoading && (
          <div className="typing-indicator">
            <span className="text-sm text-gray-600">Legal Assistant is typing</span>
            <div className="typing-dots">
              <div className="typing-dot"></div>
              <div className="typing-dot"></div>
              <div className="typing-dot"></div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSubmit} className="p-4 border-t bg-gray-50">
        <div className="flex space-x-4">
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Ask a legal question..."
            className="flex-1 border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={isLoading || !inputValue.trim()}
            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Sending...' : 'Send'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChatInterface;
