import React from 'react';
import { Trash2 } from 'lucide-react';

const FavoritesList = ({ favorites, onRemove }) => {
  if (favorites.length === 0) {
    return (
      <div className="text-center py-10 bg-white rounded-lg shadow-sm border border-gray-200">
        <p className="text-gray-500">You haven't added any favorite currencies yet.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      {favorites.map((fav) => (
        <div 
          key={fav.code} 
          className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm flex items-center justify-between hover:shadow-md transition-shadow"
        >
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
              {fav.symbol || fav.code.charAt(0)}
            </div>
            <div>
              <p className="font-bold text-gray-900">{fav.code}</p>
              <p className="text-xs text-gray-500 truncate w-32" title={fav.name}>{fav.name}</p>
            </div>
          </div>
          <button
            onClick={() => onRemove(fav.code)}
            className="text-red-400 hover:text-red-600 p-2 rounded-full hover:bg-red-50 transition-colors focus:outline-none"
            title="Remove from favorites"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ))}
    </div>
  );
};

export default FavoritesList;
