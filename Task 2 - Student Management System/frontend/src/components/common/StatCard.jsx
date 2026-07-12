import React from 'react'

/**
 * Reusable component for displaying a summary statistic.
 *
 * @param {string} title - The label for the stat (e.g. "Total Students")
 * @param {string|number} value - The numeric or string value to display
 * @param {React.ElementType} icon - A Lucide React icon component
 * @param {string} iconColor - Tailwind text color class for the icon (e.g. "text-primary")
 * @param {string} iconBg - Tailwind bg color class for the icon wrapper (e.g. "bg-primary/10")
 */
function StatCard({ title, value, icon: Icon, iconColor = 'text-primary', iconBg = 'bg-primary/10' }) {
  return (
    <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-6 flex items-center gap-4">
      {Icon && (
        <div className={`w-12 h-12 rounded-full flex items-center justify-center shrink-0 ${iconBg} ${iconColor}`}>
          <Icon size={24} aria-hidden="true" />
        </div>
      )}
      <div>
        <p className="text-sm font-medium text-slate-500 mb-1">{title}</p>
        <p className="font-heading text-2xl font-semibold text-slate-900">{value}</p>
      </div>
    </div>
  )
}

export default StatCard
