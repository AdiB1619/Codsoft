import { useNavigate } from 'react-router-dom'
import { Eye, Pencil, Trash2, ChevronUp, ChevronDown, ChevronsUpDown } from 'lucide-react'
import Badge from '../common/Badge'
import { formatFullName } from '../../utils/formatters'

/**
 * Sortable column header — renders the column label with an up/down indicator
 * when that column is the active sort.
 */
function SortHeader({ label, field, currentSortBy, currentSortDir, onSort }) {
  const isActive = currentSortBy === field

  let Icon = ChevronsUpDown
  if (isActive) Icon = currentSortDir === 'asc' ? ChevronUp : ChevronDown

  return (
    <button
      onClick={() => onSort(field)}
      className={`inline-flex items-center gap-1 text-xs font-medium uppercase tracking-wide
        transition-colors duration-hover focus-ring rounded
        ${isActive ? 'text-primary' : 'text-slate-500 hover:text-slate-700'}`}
      aria-label={`Sort by ${label}`}
      aria-pressed={isActive}
    >
      {label}
      <Icon size={13} aria-hidden="true" />
    </button>
  )
}

/**
 * StudentTable — pure presentational component.
 *
 * Displays a paginated list of students per Section 8.4 wireframe.
 * Columns: Roll No | Name | Email | Course | Status | Actions
 *
 * All data-fetching state lives in the parent (StudentListPage via useStudents).
 * This component only renders what it receives via props.
 *
 * @param {StudentResponseDTO[]} students
 * @param {string} sortBy    - active sort column key
 * @param {'asc'|'desc'} sortDir
 * @param {(field: string) => void} onSort
 * @param {(id: number) => void} onDelete
 */
function StudentTable({ students, sortBy, sortDir, onSort, onDelete }) {
  const navigate = useNavigate()

  const SORTABLE_COLUMNS = [
    { label: 'Roll No',  field: 'rollNumber' },
    { label: 'Name',     field: 'firstName'  },
    { label: 'Email',    field: 'email'      },
    { label: 'Course',   field: null         },   // not sortable
    { label: 'Status',   field: 'status'     },
    { label: 'Actions',  field: null         },
  ]

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="w-full text-sm text-left" aria-label="Student list">
        {/* ── Table head ──────────────────────────────────── */}
        <thead className="bg-slate-50 border-b border-slate-200">
          <tr>
            {SORTABLE_COLUMNS.map(({ label, field }) => (
              <th
                key={label}
                scope="col"
                className="px-4 py-3 whitespace-nowrap"
              >
                {field ? (
                  <SortHeader
                    label={label}
                    field={field}
                    currentSortBy={sortBy}
                    currentSortDir={sortDir}
                    onSort={onSort}
                  />
                ) : (
                  <span className="text-xs font-medium uppercase tracking-wide text-slate-500">
                    {label}
                  </span>
                )}
              </th>
            ))}
          </tr>
        </thead>

        {/* ── Table body ──────────────────────────────────── */}
        <tbody className="divide-y divide-slate-100 bg-white">
          {students.map((student) => (
            <tr
              key={student.id}
              className="hover:bg-slate-50 transition-colors duration-hover"
            >
              {/* Roll Number */}
              <td className="px-4 py-3 whitespace-nowrap">
                <span className="font-mono text-xs text-slate-600 bg-slate-100
                  px-2 py-0.5 rounded">
                  {student.rollNumber}
                </span>
              </td>

              {/* Full Name */}
              <td className="px-4 py-3 whitespace-nowrap font-medium text-slate-800">
                {formatFullName(student.firstName, student.lastName)}
              </td>

              {/* Email */}
              <td className="px-4 py-3 text-slate-600 max-w-[200px] truncate">
                <a
                  href={`mailto:${student.email}`}
                  className="hover:text-primary transition-colors duration-hover focus-ring rounded"
                >
                  {student.email}
                </a>
              </td>

              {/* Course */}
              <td className="px-4 py-3 whitespace-nowrap text-slate-600">
                {student.course?.courseName ?? '—'}
              </td>

              {/* Status */}
              <td className="px-4 py-3 whitespace-nowrap">
                <Badge status={student.status} />
              </td>

              {/* Actions */}
              <td className="px-4 py-3 whitespace-nowrap">
                <div className="flex items-center gap-1">
                  {/* View */}
                  <button
                    onClick={() => navigate(`/students/${student.id}`)}
                    aria-label={`View ${formatFullName(student.firstName, student.lastName)}`}
                    title="View details"
                    className="p-1.5 rounded text-slate-400 hover:text-primary hover:bg-primary/5
                      transition-colors duration-hover focus-ring"
                  >
                    <Eye size={15} aria-hidden="true" />
                  </button>

                  {/* Edit */}
                  <button
                    onClick={() => navigate(`/students/${student.id}/edit`)}
                    aria-label={`Edit ${formatFullName(student.firstName, student.lastName)}`}
                    title="Edit student"
                    className="p-1.5 rounded text-slate-400 hover:text-accent hover:bg-accent/5
                      transition-colors duration-hover focus-ring"
                  >
                    <Pencil size={15} aria-hidden="true" />
                  </button>

                  {/* Delete */}
                  <button
                    onClick={() => onDelete(student)}
                    aria-label={`Delete ${formatFullName(student.firstName, student.lastName)}`}
                    title="Delete student"
                    className="p-1.5 rounded text-slate-400 hover:text-danger hover:bg-danger/5
                      transition-colors duration-hover focus-ring"
                  >
                    <Trash2 size={15} aria-hidden="true" />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default StudentTable
