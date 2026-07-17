import apiClient from './apiClient';

/**
 * Currency Services
 */
export const currencyService = {
  getSupportedCurrencies: async () => {
    const response = await apiClient.get('/currencies');
    return response.data;
  },
};

export const conversionService = {
  convertCurrency: async ({ from, to, amount }) => {
    const response = await apiClient.post('/convert', { from, to, amount });
    return response.data;
  },
};

export const historyService = {
  getHistory: async () => {
    const response = await apiClient.get('/history');
    return response.data;
  },
  
  deleteHistory: async (id) => {
    await apiClient.delete(`/history/${id}`);
  }
};

export const favoritesService = {
  getFavorites: async () => {
    const response = await apiClient.get('/favorites');
    return response.data;
  },
  
  addFavorite: async (currencyCode) => {
    const response = await apiClient.post('/favorites', { currency: currencyCode });
    return response.data;
  },
  
  removeFavorite: async (currencyCode) => {
    await apiClient.delete(`/favorites/${currencyCode}`);
  }
};
