import { ChevronLeft, ChevronRight } from 'lucide-react'

/**
 * Pagination — page navigation controls.
 *
 * @param {number} currentPage - zero-indexed current page
 * @param {number} totalPages
 * @param {(page: number) => void} onPageChange
 */
function Pagination({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  return (
    <nav
      className="flex items-center justify-between mt-4"
      aria-label="Pagination"
    >
      <p className="text-sm text-slate-500">
        Page {currentPage + 1} of {totalPages}
      </p>
      <div className="flex gap-1">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          aria-label="Previous page"
          className="p-1.5 rounded-md text-slate-500 hover:bg-slate-100 disabled:opacity-40
            disabled:cursor-not-allowed transition-colors duration-hover focus-ring"
        >
          <ChevronLeft size={16} aria-hidden="true" />
        </button>
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
          aria-label="Next page"
          className="p-1.5 rounded-md text-slate-500 hover:bg-slate-100 disabled:opacity-40
            disabled:cursor-not-allowed transition-colors duration-hover focus-ring"
        >
          <ChevronRight size={16} aria-hidden="true" />
        </button>
      </div>
    </nav>
  )
}

export default Pagination
