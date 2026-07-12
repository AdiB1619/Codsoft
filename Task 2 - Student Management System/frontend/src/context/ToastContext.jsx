import { createContext, useContext, useState, useCallback } from 'react'
import Toast from '../components/common/Toast'

const ToastContext = createContext(null)

let nextId = 0

/**
 * ToastProvider — makes the toast API available to all child components.
 *
 * Usage:
 *   const { showToast } = useToast()
 *   showToast('Saved!', 'success')          // type: 'success'|'error'|'warning'|'info'
 *
 * This provider is domain-agnostic; it knows nothing about students or courses.
 */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const showToast = useCallback((message, type = 'info') => {
    const id = ++nextId
    setToasts(prev => [...prev, { id, message, type }])
    // Auto-dismiss after 4 seconds — SDD Section 9.8 (toast fade)
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id))
    }, 4000)
  }, [])

  const dismiss = useCallback((id) => {
    setToasts(prev => prev.filter(t => t.id !== id))
  }, [])

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      {/*
        Toast container — SDD Section 9.6:
          aria-live="polite" ensures screen readers announce each toast without
          interrupting the current reading flow.
      */}
      <div
        aria-live="polite"
        aria-atomic="false"
        className="fixed bottom-4 right-4 flex flex-col gap-2 z-50 pointer-events-none"
      >
        {toasts.map(toast => (
          <div key={toast.id} className="pointer-events-auto">
            <Toast toast={toast} onDismiss={dismiss} />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToastContext() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToastContext must be used inside <ToastProvider>')
  return ctx
}
