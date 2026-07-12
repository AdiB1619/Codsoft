/**
 * Loader — full-area spinner used during data fetching.
 * @param {string} label - accessible screen-reader label
 */
function Loader({ label = 'Loading…' }) {
  return (
    <div role="status" className="flex items-center justify-center py-16 gap-3 text-slate-400">
      <svg
        className="animate-spin h-6 w-6 text-primary/50"
        viewBox="0 0 24 24"
        fill="none"
        aria-hidden="true"
      >
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
      </svg>
      <span className="text-sm">{label}</span>
    </div>
  )
}

export default Loader
