import { Search } from 'lucide-react'

/**
 * SearchBar — text input for student search.
 * The parent is responsible for debouncing (via useDebounce).
 *
 * @param {string} value
 * @param {(value: string) => void} onChange
 * @param {string} placeholder
 */
function SearchBar({ value, onChange, placeholder = 'Search…' }) {
  return (
    <div className="relative">
      <Search
        size={16}
        className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
        aria-hidden="true"
      />
      <input
        type="search"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        aria-label={placeholder}
        className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-md bg-white
          placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-primary
          focus:border-primary transition-colors duration-hover"
      />
    </div>
  )
}

export default SearchBar
