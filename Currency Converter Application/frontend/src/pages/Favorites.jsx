import React, { useState, useEffect } from 'react';
import { favoritesService, currencyService } from '../api/services';
import { useToast } from '../components/Toast';
import FavoritesList from '../components/FavoritesList';
import LoadingSpinner from '../components/LoadingSpinner';
import { Star, Plus } from 'lucide-react';

const Favorites = () => {
  const [favorites, setFavorites] = useState([]);
  const [allCurrencies, setAllCurrencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newFavoriteCode, setNewFavoriteCode] = useState('');
  const [isAdding, setIsAdding] = useState(false);
  
  const addToast = useToast();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [favsData, currenciesData] = await Promise.all([
          favoritesService.getFavorites(),
          currencyService.getSupportedCurrencies()
        ]);
        
        // Map the array of currency code strings to full currency objects
        const favoriteObjects = favsData
          .map(code => currenciesData.find(c => c.code === code))
          .filter(Boolean); // filter out any that weren't found
          
        setFavorites(favoriteObjects);
        setAllCurrencies(currenciesData);
      } catch (error) {
        addToast(error.message || 'Failed to load favorites data', 'error');
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, [addToast]);

  const handleAddFavorite = async (e) => {
    e.preventDefault();
    if (!newFavoriteCode) return;
    
    // Check if already in favorites
    if (favorites.some(f => f.code === newFavoriteCode)) {
      addToast(`${newFavoriteCode} is already in your favorites`, 'info');
      setNewFavoriteCode('');
      return;
    }
    
    setIsAdding(true);
    try {
      await favoritesService.addFavorite(newFavoriteCode);
      
      // Find the currency details from allCurrencies to update UI immediately
      const currencyDetails = allCurrencies.find(c => c.code === newFavoriteCode);
      
      if (currencyDetails) {
        setFavorites(prev => [...prev, currencyDetails].sort((a, b) => a.code.localeCompare(b.code)));
        addToast(`${newFavoriteCode} added to favorites`, 'success');
      }
      
      setNewFavoriteCode('');
    } catch (error) {
      addToast(error.message || 'Failed to add favorite', 'error');
    } finally {
      setIsAdding(false);
    }
  };

  const handleRemoveFavorite = async (code) => {
    try {
      await favoritesService.removeFavorite(code);
      setFavorites(prev => prev.filter(f => f.code !== code));
      addToast(`${code} removed from favorites`, 'success');
    } catch (error) {
      addToast(error.message || 'Failed to remove favorite', 'error');
    }
  };

  // Filter out currencies that are already in favorites for the dropdown
  const availableCurrencies = allCurrencies.filter(
    c => !favorites.some(f => f.code === c.code)
  );

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full min-h-[60vh]">
        <LoadingSpinner size="lg" text="Loading favorites..." />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center mb-6">
          <Star className="mr-2 text-yellow-500" size={24} />
          Favorite Currencies
        </h1>
        
        <form onSubmit={handleAddFavorite} className="flex flex-col sm:flex-row items-end space-y-4 sm:space-y-0 sm:space-x-4 max-w-lg">
          <div className="w-full">
            <label htmlFor="currency-select" className="block text-sm font-medium text-gray-700 mb-1">
              Add New Favorite
            </label>
            <select
              id="currency-select"
              value={newFavoriteCode}
              onChange={(e) => setNewFavoriteCode(e.target.value)}
              className="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md border"
              disabled={isAdding}
            >
              <option value="" disabled>Select a currency</option>
              {availableCurrencies.map(c => (
                <option key={c.code} value={c.code}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>
          
          <button
            type="submit"
            disabled={!newFavoriteCode || isAdding}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none disabled:bg-blue-400 disabled:cursor-not-allowed transition-colors"
          >
            {isAdding ? (
              <LoadingSpinner size="sm" text="" />
            ) : (
              <>
                <Plus size={18} className="mr-1" />
                Add
              </>
            )}
          </button>
        </form>
      </div>

      <FavoritesList favorites={favorites} onRemove={handleRemoveFavorite} />
    </div>
  );
};

export default Favorites;
