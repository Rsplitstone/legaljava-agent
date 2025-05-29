import axios from 'axios';

export async function createEvent(event: { id: string; title: string; start: string; end?: string }) {
  try {
    const response = await axios.post('/api/events', event);
    return response.data;
  } catch (error) {
    console.error('Error creating event:', error);
    throw error;
  }
}
