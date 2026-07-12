import { useEffect, useRef, useCallback } from 'react'
import { X } from 'lucide-react'
import Button from './Button'

/**
 * ConfirmModal — accessible modal dialog for confirming any action.
 *
 * Accessibility (SDD Section 9.6):
 *  - role="dialog" + aria-modal="true" for AT announcement
 *  - Focus moves to the confirm button on open
 *  - Tab key is trapped inside the dialog while it is open
 *  - Escape closes the dialog
 *  - Background scroll is prevented while open
 *
 * This is a pure UI primitive — no student/course domain knowledge.
 *
 * @param {boolean}  isOpen
 * @param {()=>void} onClose
 * @param {()=>void} onConfirm
 * @param {string}   title
 * @param {string}   message
 * @param {string}   confirmLabel  - text on the confirm button (default: 'Confirm')
 * @param {'danger'|'primary'} confirmVariant
 * @param {boolean}  loading
 */
function ConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmLabel = 'Confirm',
  confirmVariant = 'danger',
  loading = false,
}) {
  const dialogRef  = useRef(null)
  const confirmRef = useRef(null)

  // ── Focus management ────────────────────────────────────────────────────────
  useEffect(() => {
    if (isOpen) {
      // Small tick so the element is visible before focus()
      const timer = setTimeout(() => confirmRef.current?.focus(), 10)
      return () => clearTimeout(timer)
    }
  }, [isOpen])

  // ── Escape to close ─────────────────────────────────────────────────────────
  useEffect(() => {
    const handleKey = (e) => { if (e.key === 'Escape' && !loading) onClose() }
    if (isOpen) document.addEventListener('keydown', handleKey)
    return () => document.removeEventListener('keydown', handleKey)
  }, [isOpen, loading, onClose])

  // ── Focus trap ──────────────────────────────────────────────────────────────
  const trapFocus = useCallback((e) => {
    if (!dialogRef.current) return
    const focusable = dialogRef.current.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])',
    )
    const first = focusable[0]
    const last  = focusable[focusable.length - 1]

    if (e.key === 'Tab') {
      if (e.shiftKey) {
        if (document.activeElement === first) { e.preventDefault(); last?.focus() }
      } else {
        if (document.activeElement === last)  { e.preventDefault(); first?.focus() }
      }
    }
  }, [])

  useEffect(() => {
    if (isOpen) document.addEventListener('keydown', trapFocus)
    return () => document.removeEventListener('keydown', trapFocus)
  }, [isOpen, trapFocus])

  // ── Scroll lock ─────────────────────────────────────────────────────────────
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
      return () => { document.body.style.overflow = '' }
    }
  }, [isOpen])

  if (!isOpen) return null

  return (
    /* Backdrop */
    <div
      className="fixed inset-0 z-40 flex items-center justify-center bg-black/40"
      onClick={(e) => { if (e.target === e.currentTarget && !loading) onClose() }}
    >
      {/* Dialog panel */}
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="confirm-modal-title"
        aria-describedby="confirm-modal-desc"
        className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4 p-6"
      >
        {/* Header */}
        <div className="flex items-start justify-between gap-4">
          <h2
            id="confirm-modal-title"
            className="font-heading font-semibold text-slate-800"
          >
            {title}
          </h2>
          <button
            onClick={onClose}
            disabled={loading}
            aria-label="Close dialog"
            className="text-slate-400 hover:text-slate-600 transition-colors duration-hover
              focus-ring rounded disabled:opacity-50"
          >
            <X size={18} aria-hidden="true" />
          </button>
        </div>

        {/* Body */}
        <p id="confirm-modal-desc" className="mt-3 text-sm text-slate-600 leading-relaxed">
          {message}
        </p>

        {/* Actions */}
        <div className="flex justify-end gap-2 mt-6">
          <Button variant="ghost" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button
            ref={confirmRef}
            variant={confirmVariant}
            onClick={onConfirm}
            loading={loading}
          >
            {confirmLabel}
          </Button>
        </div>
      </div>
    </div>
  )
}

export default ConfirmModal
