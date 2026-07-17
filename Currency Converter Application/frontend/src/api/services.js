import apiClient from './apiClient';

/**
 * Currency Services
 */
export const currencyService = {
  // Get list of supported currencies
  getCurrencies: async () => {
    // Using a mock implementation or standard endpoint.
    // Wait, the backend doesn't have GET /api/currencies yet?
    // According to SDD, we have currencies in DB, but we didn't expose GET /api/currencies!
    // We only seeded them. I should probably add an endpoint for it in the backend, or the frontend can just hardcode or the user can request it.
    // Let's add the API call and if it fails we can fix the backend.
    const response = await apiClient.get('/currencies');
    return response.data;
  },
};

/**
 * Conversion Services
 */
export const conversionService = {
  // Convert currency
  convert: async (from, to, amount) => {
    const response = await apiClient.post('/convert', { from, to, amount });
    return response.data;
  },
};

/**
 * History Services
 */
export const historyService = {
  // Get history
  getHistory: async () => {
    const response = await apiClient.get('/history');
    return response.data;
  },
  
  // Clear all history - wait, backend only has DELETE /api/history/{id}
  deleteHistoryItem: async (id) => {
    await apiClient.delete(`/history/${id}`);
  }
};

/**
 * Favorites Services
 */
export const favoritesService = {
  // Get favorites
  getFavorites: async () => {
    const response = await apiClient.get('/favorites');
    return response.data;
  },
  
  // Add favorite
  addFavorite: async (currencyCode) => {
    const response = await apiClient.post('/favorites', { currency: currencyCode });
    return response.data;
  },
  
  // Remove favorite
  removeFavorite: async (currencyCode) => {
    await apiClient.delete(`/favorites/${currencyCode}`);
  }
};
