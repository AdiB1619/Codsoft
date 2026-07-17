import axios from 'axios';

// Create a configured axios instance
// Thanks to Vite's proxy, we can just use /api
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for generic error handling
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // We can handle global error side effects here if we want (e.g., throwing custom Error objects)
    // The components will catch and display these errors.
    const message = error.response?.data?.message || error.message || 'An unexpected error occurred';
    return Promise.reject(new Error(message));
  }
);

export default apiClient;
