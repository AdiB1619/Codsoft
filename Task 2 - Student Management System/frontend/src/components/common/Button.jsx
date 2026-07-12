import { forwardRef } from 'react'

/**
 * Button — primary interactive element.
 *
 * Uses forwardRef so callers (e.g., ConfirmModal) can programmatically focus
 * the button for accessibility (Section 9.6).
 *
 * @param {'primary'|'secondary'|'danger'|'ghost'} variant
 * @param {'sm'|'md'|'lg'} size
 * @param {boolean} loading - shows a spinner and disables interaction
 * @param {string} className - additional classes merged onto the element
 */
const Button = forwardRef(function Button(
  { children, variant = 'primary', size = 'md', loading = false, className = '', ...props },
  ref,
) {
  const base =
    'inline-flex items-center justify-center gap-2 font-medium rounded-md ' +
    'transition-colors duration-hover focus-ring ' +
    'disabled:opacity-60 disabled:cursor-not-allowed'

  const variants = {
    primary:   'bg-primary text-white hover:bg-primary-hover',
    secondary: 'bg-accent text-white hover:bg-accent/90',
    danger:    'bg-danger text-white hover:bg-danger/90',
    ghost:     'bg-transparent text-primary border border-primary hover:bg-primary/5',
  }

  const sizes = {
    sm: 'text-xs px-2.5 py-1.5',
    md: 'text-sm px-4 py-2',
    lg: 'text-base px-6 py-3',
  }

  return (
    <button
      ref={ref}
      className={`${base} ${variants[variant] ?? variants.primary} ${sizes[size] ?? sizes.md} ${className}`}
      disabled={loading || props.disabled}
      {...props}
    >
      {loading && (
        <svg
          className="animate-spin h-4 w-4 shrink-0"
          viewBox="0 0 24 24"
          fill="none"
          aria-hidden="true"
        >
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
        </svg>
      )}
      {children}
    </button>
  )
})

export default Button
