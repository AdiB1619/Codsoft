/**
 * FilterDropdown — generic labelled select control.
 *
 * @param {string} label - visible + accessible label
 * @param {string} value - currently selected value
 * @param {(value: string) => void} onChange
 * @param {{ label: string, value: string }[]} options
 */
function FilterDropdown({ label, value, onChange, options }) {
  const id = `filter-${label.toLowerCase().replace(/\s+/g, '-')}`
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-xs font-medium text-slate-500 uppercase tracking-wide">
        {label}
      </label>
      <select
        id={id}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="text-sm border border-slate-200 rounded-md px-3 py-2 bg-white text-slate-700
          focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary
          transition-colors duration-hover"
      >
        <option value="">All</option>
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  )
}

export default FilterDropdown
