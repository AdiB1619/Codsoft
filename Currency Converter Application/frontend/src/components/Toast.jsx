import React, { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext();

export const useToast = () => useContext(ToastContext);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((message, type = 'info') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
    
    // Auto remove after 3s
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 3000);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div style={{ position: 'fixed', bottom: '2rem', right: '2rem', display: 'flex', flexDirection: 'column', gap: '1rem', zIndex: 9999 }}>
        {toasts.map(toast => (
          <div key={toast.id} className="animate-fade-in glass-panel" style={{ 
            padding: '1rem 1.5rem', 
            borderRadius: 'var(--radius-md)',
            borderLeft: `4px solid ${toast.type === 'error' ? 'var(--color-error)' : 'var(--color-primary)'}`,
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            backgroundColor: 'white',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
          }}>
            <strong style={{ textTransform: 'capitalize', color: toast.type === 'error' ? 'var(--color-error)' : 'var(--color-primary)' }}>
              {toast.type}:
            </strong> 
            <span>{toast.message}</span>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};
