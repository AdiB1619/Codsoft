import React, { useState, useEffect } from 'react';
import { Trash2, Plus, Star } from 'lucide-react';
import { favoritesService, currencyService } from '../api/services';
import Spinner from '../components/Spinner';
import CustomSelect from '../components/CustomSelect';
import { useToast } from '../components/Toast';

const Favorites = () => {
  const { showToast } = useToast();
  const [favorites, setFavorites] = useState([]);
  const [currencies, setCurrencies] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [newFavorite, setNewFavorite] = useState('');
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    // Load favorites and currencies
    Promise.all([
      favoritesService.getFavorites(),
      currencyService.getCurrencies()
    ])
    .then(([favData, curData]) => {
      setFavorites(favData);
      setCurrencies(curData);
      setLoading(false);
    })
    .catch(err => {
      showToast(err.message, 'error');
      setLoading(false);
    });
  }, [showToast]);

  const handleAddFavorite = async (e) => {
    e.preventDefault();
    if (!newFavorite) return;
    
    setAdding(true);
    try {
      await favoritesService.addFavorite(newFavorite);
      showToast(`${newFavorite} added to favorites`, 'success');
      setFavorites([...favorites, newFavorite]);
      setNewFavorite('');
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setAdding(false);
    }
  };

  const handleRemoveFavorite = async (code) => {
    try {
      await favoritesService.removeFavorite(code);
      showToast(`${code} removed from favorites`, 'success');
      setFavorites(favorites.filter(fav => fav !== code));
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Filter out currencies that are already in favorites for the dropdown
  const availableCurrencies = currencies.filter(c => !favorites.includes(c.code));

  return (
    <div className="container animate-fade-in" style={{ maxWidth: '800px' }}>
      <div className="text-center mb-8">
        <h2>Favorite Currencies</h2>
        <p className="text-muted">Manage your frequently used currencies.</p>
      </div>

      <div className="glass-panel mb-8" style={{ padding: 'var(--spacing-6)' }}>
        <h3 className="mb-4">Add to Favorites</h3>
        <form onSubmit={handleAddFavorite} style={{ display: 'flex', gap: 'var(--spacing-4)', alignItems: 'flex-end' }}>
          <div style={{ flex: 1 }}>
            <CustomSelect 
              label="Select Currency"
              value={newFavorite}
              onChange={(e) => setNewFavorite(e.target.value)}
              options={availableCurrencies}
              disabled={loading || availableCurrencies.length === 0}
            />
          </div>
          <button 
            type="submit" 
            className="btn btn-primary mb-4" 
            disabled={!newFavorite || adding}
          >
            {adding ? <Spinner size={18} text="" /> : <Plus size={20} />} Add
          </button>
        </form>
        {availableCurrencies.length === 0 && !loading && (
          <p className="text-muted" style={{ fontSize: '0.875rem' }}>All supported currencies are already in your favorites!</p>
        )}
      </div>

      <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
        <h3 className="mb-4">Your Favorites</h3>
        {loading ? (
          <Spinner text="Loading favorites..." />
        ) : favorites.length === 0 ? (
          <div className="text-center text-muted" style={{ padding: 'var(--spacing-6)' }}>
            <Star size={32} className="mb-2" style={{ opacity: 0.5 }} />
            <p>You haven't added any favorites yet.</p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 'var(--spacing-4)' }}>
            {favorites.map(code => {
              const cur = currencies.find(c => c.code === code);
              return (
                <div key={code} className="glass-panel" style={{ 
                  padding: 'var(--spacing-4)', 
                  display: 'flex', 
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  backgroundColor: 'var(--color-surface)'
                }}>
                  <div>
                    <div style={{ fontWeight: 'bold', color: 'var(--color-primary)' }}>{code}</div>
                    <div className="text-muted" style={{ fontSize: '0.875rem' }}>{cur?.name || 'Unknown'}</div>
                  </div>
                  <button 
                    className="btn-ghost" 
                    onClick={() => handleRemoveFavorite(code)}
                    title="Remove from favorites"
                  >
                    <Trash2 size={18} className="text-error" />
                  </button>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default Favorites;
