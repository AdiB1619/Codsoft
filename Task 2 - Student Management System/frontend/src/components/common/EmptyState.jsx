import { Users } from 'lucide-react'

/**
 * EmptyState — shown when a query returns zero results.
 *
 * @param {string} title
 * @param {string} description
 * @param {React.ReactNode} action - optional CTA button
 */
function EmptyState({ title = 'No results', description = '', action }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-4 text-center text-slate-400">
      <Users size={40} className="text-slate-300" aria-hidden="true" />
      <div>
        <p className="font-heading font-semibold text-slate-500">{title}</p>
        {description && <p className="text-sm mt-1">{description}</p>}
      </div>
      {action}
    </div>
  )
}

export default EmptyState
