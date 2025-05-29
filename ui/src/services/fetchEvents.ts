import axios from 'axios';

export async function fetchEvents() {
  try {
    const response = await axios.get('/api/events');
    return response.data;
  } catch (error) {
    console.error('Error fetching events:', error);
    throw error;
  }
}
