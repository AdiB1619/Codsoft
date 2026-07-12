/** Status → Tailwind badge colour map — SDD Section 9.2 */
const STATUS_STYLES = {
  ACTIVE:    'bg-success/10 text-success',
  INACTIVE:  'bg-slate-100 text-slate-500',
  GRADUATED: 'bg-accent/10 text-accent',
  SUSPENDED: 'bg-warning/10 text-warning',
}

/**
 * Badge — semantic coloured pill for student status values.
 *
 * @param {'ACTIVE'|'INACTIVE'|'GRADUATED'|'SUSPENDED'} status
 */
function Badge({ status }) {
  const style = STATUS_STYLES[status] ?? 'bg-slate-100 text-slate-500'
  return (
    <span className={`badge ${style}`}>
      {status}
    </span>
  )
}

export default Badge
