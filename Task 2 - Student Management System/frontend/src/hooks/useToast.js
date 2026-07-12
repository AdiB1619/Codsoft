import { useToastContext } from '../context/ToastContext'

/**
 * useToast — convenience wrapper around ToastContext so callers import one
 * hook instead of using useContext directly.
 *
 * @returns {{ showToast: (message: string, type?: 'success'|'error'|'warning') => void }}
 */
function useToast() {
  return useToastContext()
}

export default useToast
