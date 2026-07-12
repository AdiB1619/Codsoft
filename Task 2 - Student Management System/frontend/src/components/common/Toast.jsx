import { CheckCircle, XCircle, AlertTriangle, Info, X } from 'lucide-react'

/**
 * Toast — a single notification message rendered by ToastContext.
 *
 * This is a pure, domain-agnostic UI primitive.
 * It is only instantiated by ToastContext; callers use showToast() instead.
 *
 * @param {{ id: number, message: string, type: 'success'|'error'|'warning'|'info' }} toast
 * @param {(id: number) => void} onDismiss
 */

const VARIANTS = {
  success: {
    container: 'bg-success text-white',
    icon:      CheckCircle,
  },
  error: {
    container: 'bg-danger text-white',
    icon:      XCircle,
  },
  warning: {
    container: 'bg-warning text-white',
    icon:      AlertTriangle,
  },
  info: {
    container: 'bg-primary text-white',
    icon:      Info,
  },
}

function Toast({ toast, onDismiss }) {
  const variant = VARIANTS[toast.type] ?? VARIANTS.info
  const Icon = variant.icon

  return (
    <div
      role="status"
      aria-atomic="true"
      className={`flex items-start gap-3 pl-4 pr-3 py-3 rounded-md shadow-lg
        text-sm max-w-sm w-full ${variant.container}`}
    >
      {/* Leading icon */}
      <Icon size={18} className="shrink-0 mt-0.5" aria-hidden="true" />

      {/* Message */}
      <span className="flex-1 leading-snug">{toast.message}</span>

      {/* Dismiss */}
      <button
        onClick={() => onDismiss(toast.id)}
        aria-label="Dismiss notification"
        className="opacity-70 hover:opacity-100 transition-opacity duration-hover
          focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-1
          focus:ring-offset-transparent rounded"
      >
        <X size={16} aria-hidden="true" />
      </button>
    </div>
  )
}

export default Toast
