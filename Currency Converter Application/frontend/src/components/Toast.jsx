import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { X, CheckCircle, AlertCircle } from 'lucide-react';

const ToastContext = createContext(null);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info', duration = 5000) => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type, duration }]);
    
    if (duration > 0) {
      setTimeout(() => {
        removeToast(id);
      }, duration);
    }
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={addToast}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col space-y-2" aria-live="polite">
        {toasts.map((toast) => (
          <div 
            key={toast.id} 
            role="alert"
            className={`flex items-center justify-between p-4 min-w-[300px] max-w-sm bg-white rounded-lg shadow-lg border-l-4 transition-all transform duration-300 ${
              toast.type === 'error' ? 'border-red-500' : 
              toast.type === 'success' ? 'border-green-500' : 'border-blue-500'
            }`}
          >
            <div className="flex items-center space-x-3">
              {toast.type === 'error' && <AlertCircle className="text-red-500" size={20} />}
              {toast.type === 'success' && <CheckCircle className="text-green-500" size={20} />}
              {toast.type === 'info' && <AlertCircle className="text-blue-500" size={20} />}
              
              <p className="text-gray-700 font-medium text-sm">{toast.message}</p>
            </div>
            <button 
              onClick={() => removeToast(toast.id)}
              className="text-gray-400 hover:text-gray-600 focus:outline-none ml-4"
              aria-label="Close"
            >
              <X size={16} />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within ToastProvider');
  return context;
};
