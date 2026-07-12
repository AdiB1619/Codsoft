/**
 * Formatters — pure functions that transform raw API data into display strings.
 * No side-effects; no imports from other app modules.
 */

/**
 * Format an ISO date string (YYYY-MM-DD) into a locale-friendly string.
 * @param {string|null} isoDate
 * @returns {string}
 */
export function formatDate(isoDate) {
  if (!isoDate) return '—'
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(isoDate))
}

/**
 * Format a grade percentage value.
 * @param {number|null} grade
 * @returns {string}
 */
export function formatGrade(grade) {
  if (grade === null || grade === undefined) return 'N/A'
  return `${parseFloat(grade).toFixed(2)}%`
}

/**
 * Format a full name from first and last name fields.
 * @param {string} firstName
 * @param {string} lastName
 * @returns {string}
 */
export function formatFullName(firstName, lastName) {
  return [firstName, lastName].filter(Boolean).join(' ')
}
