import { useState, useEffect, useCallback, useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'
import studentApi from '../api/studentApi'
import useToast from './useToast'

const DEFAULT_PARAMS = {
  page:    0,
  size:    10,
  sortBy:  'id',
  sortDir: 'asc',
  search:  '',
  courseId: '',
  status:  '',
}

/**
 * useStudents — encapsulates all state and side-effects for the student list.
 *
 * Uses React Router's useSearchParams to reflect active filters in the URL,
 * making views bookmarkable and shareable.
 */
function useStudents() {
  const { showToast } = useToast()
  const [searchParams, setSearchParams] = useSearchParams()

  // ── 1. Derive params from URL ─────────────────────────────────────────────
  const params = useMemo(() => {
    return {
      page:    Number(searchParams.get('page')) || DEFAULT_PARAMS.page,
      size:    Number(searchParams.get('size')) || DEFAULT_PARAMS.size,
      sortBy:  searchParams.get('sortBy') || DEFAULT_PARAMS.sortBy,
      sortDir: searchParams.get('sortDir') || DEFAULT_PARAMS.sortDir,
      search:  searchParams.get('search') || DEFAULT_PARAMS.search,
      courseId: searchParams.get('courseId') || DEFAULT_PARAMS.courseId,
      status:  searchParams.get('status') || DEFAULT_PARAMS.status,
    }
  }, [searchParams])

  const [data, setData]       = useState(null)   // PagedResponse
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)   // string | null

  // ── Fetch ────────────────────────────────────────────────────────────────
  const fetchStudents = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const pagedResponse = await studentApi.getAll(params)
      setData(pagedResponse)
    } catch (err) {
      setError(err.message ?? 'Failed to load students.')
    } finally {
      setLoading(false)
    }
  }, [params])

  useEffect(() => {
    fetchStudents()
  }, [fetchStudents])

  // ── Delete ───────────────────────────────────────────────────────────────
  const deleteStudent = useCallback(async (id) => {
    try {
      await studentApi.remove(id)
      showToast('Student deleted successfully.', 'success')
      fetchStudents()
    } catch (err) {
      if (err.status === 404 || err.message?.toLowerCase().includes('not found')) {
        showToast('Student has already been deleted.', 'error')
        fetchStudents() // Refresh to remove the stale entry
      } else {
        showToast(err.message ?? 'Failed to delete student.', 'error')
      }
      throw err // Rethrow so the caller can stop the loading spinner
    }
  }, [fetchStudents, showToast])

  // ── Update status ────────────────────────────────────────────────────────
  const changeStatus = useCallback(async (id, status) => {
    try {
      await studentApi.updateStatus(id, status)
      showToast(`Student status updated to ${status}.`, 'success')
      fetchStudents()
    } catch (err) {
      showToast(err.message ?? 'Failed to update status.', 'error')
    }
  }, [fetchStudents, showToast])

  // ── Param helpers ────────────────────────────────────────────────────────
  const updateParams = useCallback((updates) => {
    setSearchParams(prev => {
      const next = new URLSearchParams(prev)
      let hasChanges = false

      Object.entries(updates).forEach(([k, v]) => {
        const strV = v === '' || v === null || v === undefined ? null : String(v)
        if (next.get(k) !== strV) hasChanges = true
      })

      if (!hasChanges) return prev

      // Reset to page 0 on any filter/search change
      next.set('page', '0')

      Object.entries(updates).forEach(([k, v]) => {
        if (v === '' || v === null || v === undefined) {
          next.delete(k)
        } else {
          next.set(k, String(v))
        }
      })
      return next
    }, { replace: true }) // use replace to not spam browser history
  }, [setSearchParams])

  const setPage = useCallback((page) => {
    setSearchParams(prev => {
      const next = new URLSearchParams(prev)
      next.set('page', String(page))
      return next
    })
  }, [setSearchParams])

  return {
    students:      data?.content       ?? [],
    totalPages:    data?.totalPages    ?? 0,
    totalElements: data?.totalElements ?? 0,
    currentPage:   params.page,
    params,
    loading,
    error,
    updateParams,
    setPage,
    deleteStudent,
    changeStatus,
    refresh: fetchStudents,
  }
}

export default useStudents
