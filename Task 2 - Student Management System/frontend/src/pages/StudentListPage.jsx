import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { UserPlus, Download, AlertCircle } from 'lucide-react'

import useStudents from '../hooks/useStudents'
import useDebounce from '../hooks/useDebounce'
import useToast from '../hooks/useToast'
import { triggerDownload } from '../utils/download'
import courseApi from '../api/courseApi'
import studentApi from '../api/studentApi'

import StudentTable from '../components/students/StudentTable'
import Loader from '../components/common/Loader'
import EmptyState from '../components/common/EmptyState'
import Pagination from '../components/common/Pagination'
import SearchBar from '../components/common/SearchBar'
import FilterDropdown from '../components/common/FilterDropdown'
import Button from '../components/common/Button'
import DeleteConfirmModal from '../components/students/DeleteConfirmModal'
import { STUDENT_STATUSES } from '../utils/constants'
import { formatFullName } from '../utils/formatters'

/**
 * StudentListPage — owns all data-fetching state via useStudents.
 *
 * Implements the full Section 8.4 wireframe:
 *  Toolbar: Search | Course filter | Status filter | Sort | Add Student | Export CSV
 *  Table:   StudentTable (presentational)
 *  Footer:  Pagination + row count
 *
 * StudentTable is kept purely presentational — it receives data and callbacks,
 * never fetches on its own.
 */
function StudentListPage() {
  const { showToast } = useToast()

  // ── Student list state via hook ──────────────────────────────────────────
  const {
    students,
    totalPages,
    totalElements,
    currentPage,
    params,
    loading,
    error,
    updateParams,
    setPage,
    deleteStudent,
    refresh,
  } = useStudents()

  // ── Search input state (raw, un-debounced) ───────────────────────────────
  const [searchInput, setSearchInput] = useState(params.search || '')
  const debouncedSearch = useDebounce(searchInput, 350)

  // ── Courses list for the filter dropdown ─────────────────────────────────
  const [courses, setCourses] = useState([])

  useEffect(() => {
    courseApi.getAll()
      .then(data => setCourses(Array.isArray(data) ? data : []))
      .catch(() => {/* courses filter is non-critical — fail silently */})
  }, [])

  // Propagate debounced search into the hook params
  useEffect(() => {
    updateParams({ search: debouncedSearch })
  }, [debouncedSearch, updateParams])

  // ── Delete confirmation modal ─────────────────────────────────────────────
  const [studentToDelete, setStudentToDelete] = useState(null)  // StudentResponseDTO | null
  const [deleting, setDeleting]               = useState(false)

  const handleDeleteRequest = useCallback((student) => {
    setStudentToDelete(student)
  }, [])

  const handleDeleteConfirm = useCallback(async () => {
    if (!studentToDelete) return
    setDeleting(true)
    try {
      await deleteStudent(studentToDelete.id)
      setStudentToDelete(null)
    } finally {
      setDeleting(false)
    }
  }, [studentToDelete, deleteStudent])

  // ── Sort toggle ───────────────────────────────────────────────────────────
  const handleSort = useCallback((field) => {
    const sameField = params.sortBy === field
    updateParams({
      sortBy:  field,
      sortDir: sameField && params.sortDir === 'asc' ? 'desc' : 'asc',
    })
  }, [params.sortBy, params.sortDir, updateParams])

  // ── CSV Export ────────────────────────────────────────────────────────────
  const [exporting, setExporting] = useState(false)

  const handleExport = useCallback(async () => {
    setExporting(true)
    try {
      const response = await studentApi.exportCsv({
        search:   params.search,
        courseId: params.courseId,
        status:   params.status,
        sortBy:   params.sortBy,
        sortDir:  params.sortDir,
      })
      
      triggerDownload(response, `students-export-${new Date().toISOString().slice(0, 10)}.csv`)
      showToast('CSV exported successfully.', 'success')
    } catch (err) {
      showToast(err.message ?? 'Export failed.', 'error')
    } finally {
      setExporting(false)
    }
  }, [params, showToast])

  // ── Course dropdown options ───────────────────────────────────────────────
  const courseOptions = courses.map(c => ({ label: c.courseName, value: String(c.id) }))
  const statusOptions = STUDENT_STATUSES.map(s => ({ label: s, value: s }))

  return (
    <div>
      {/* ── Page header ───────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 className="font-heading text-2xl font-semibold text-primary">Students</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            {totalElements > 0
              ? `${totalElements} student${totalElements !== 1 ? 's' : ''} found`
              : 'Manage student records'}
          </p>
        </div>
        <Link
          to="/students/new"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-primary text-white
            text-sm font-medium hover:bg-primary-hover transition-colors duration-hover
            focus-ring self-start sm:self-auto"
        >
          <UserPlus size={16} aria-hidden="true" />
          Add Student
        </Link>
      </div>

      {/* ── Toolbar ───────────────────────────────────────── */}
      <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-4 mb-4">
        <div className="flex flex-col lg:flex-row gap-3">
          {/* Search */}
          <div className="flex-1">
            <SearchBar
              value={searchInput}
              onChange={setSearchInput}
              placeholder="Search by name, email, roll number…"
            />
          </div>

          {/* Filters */}
          <div className="flex flex-wrap gap-3">
            <FilterDropdown
              label="Course"
              value={params.courseId}
              onChange={(v) => updateParams({ courseId: v })}
              options={courseOptions}
            />
            <FilterDropdown
              label="Status"
              value={params.status}
              onChange={(v) => updateParams({ status: v })}
              options={statusOptions}
            />
          </div>

          {/* Export */}
          <div className="flex items-end">
            <Button
              variant="ghost"
              size="sm"
              onClick={handleExport}
              loading={exporting}
              aria-label="Export students as CSV"
              className="whitespace-nowrap"
            >
              <Download size={14} aria-hidden="true" />
              Export CSV
            </Button>
          </div>
        </div>
      </div>

      {/* ── Content area ──────────────────────────────────── */}
      <div className="bg-white rounded-lg border border-slate-200 shadow-sm">

        {/* Loading */}
        {loading && <Loader label="Loading students…" />}

        {/* Error */}
        {!loading && error && (
          <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
            <AlertCircle size={36} className="text-danger/50" aria-hidden="true" />
            <p className="text-sm font-medium text-slate-700">Failed to load students</p>
            <p className="text-xs text-slate-500">{error}</p>
            <Button variant="ghost" size="sm" onClick={refresh}>Try again</Button>
          </div>
        )}

        {/* Empty state */}
        {!loading && !error && students.length === 0 && (
          <EmptyState
            title="No students found"
            description={
              params.search || params.status || params.courseId
                ? 'Try adjusting your filters or search query.'
                : 'Get started by adding your first student.'
            }
            action={
              !params.search && !params.status && !params.courseId
                ? (
                  <Link
                    to="/students/new"
                    className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-primary
                      text-white text-sm font-medium hover:bg-primary-hover
                      transition-colors duration-hover focus-ring"
                  >
                    <UserPlus size={15} aria-hidden="true" />
                    Add First Student
                  </Link>
                )
                : undefined
            }
          />
        )}

        {/* Table */}
        {!loading && !error && students.length > 0 && (
          <>
            <StudentTable
              students={students}
              sortBy={params.sortBy}
              sortDir={params.sortDir}
              onSort={handleSort}
              onDelete={handleDeleteRequest}
            />

            {/* Pagination */}
            <div className="px-4 py-3 border-t border-slate-100">
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={setPage}
              />
            </div>
          </>
        )}
      </div>

      {/* ── Delete confirmation modal ──────────────────────── */}
      <DeleteConfirmModal
        student={studentToDelete}
        onClose={() => !deleting && setStudentToDelete(null)}
        onConfirm={handleDeleteConfirm}
        isDeleting={deleting}
      />
    </div>
  )
}

export default StudentListPage
