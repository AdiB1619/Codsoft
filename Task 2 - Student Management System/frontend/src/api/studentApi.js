import axiosInstance from './axiosInstance'

/**
 * studentApi — all REST calls to /api/v1/students.
 *
 * Every function returns a Promise that resolves to the already-unwrapped
 * payload (the success interceptor in axiosInstance strips the ApiResponse
 * envelope). Callers never touch response.data.data.
 *
 * Called only from hooks (useStudents) or page components, never directly
 * from reusable UI components.
 */
const studentApi = {
  // ── §7.x GET /students/stats  ───────────────────────────────────────────
  /**
   * Retrieves aggregated statistics for the dashboard.
   * @returns {Promise<DashboardStatsDTO>}
   */
  getStats: () => axiosInstance.get('/students/stats'),

  // ── §7.4  GET /students  ─────────────────────────────────────────────────
  /**
   * Fetch a paginated, filtered, sorted page of students.
   * @param {{
   *   page?: number, size?: number,
   *   sortBy?: string, sortDir?: 'asc'|'desc',
   *   search?: string, courseId?: number|'', status?: string
   * }} params
   * @returns {Promise<{content: StudentResponseDTO[], totalPages: number, totalElements: number, ...}>}
   */
  getAll: (params = {}) => {
    // Strip empty string values so they don't pollute the query string
    const clean = Object.fromEntries(
      Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined),
    )
    return axiosInstance.get('/students', { params: clean })
  },

  // ── §7.5  GET /students/:id  ─────────────────────────────────────────────
  /** @returns {Promise<StudentResponseDTO>} */
  getById: (id) => axiosInstance.get(`/students/${id}`),

  // ── §7.3  POST /students  ────────────────────────────────────────────────
  /** @returns {Promise<StudentResponseDTO>} */
  create: (data) => axiosInstance.post('/students', data),

  // ── §7.6  PUT /students/:id  ─────────────────────────────────────────────
  /** @returns {Promise<StudentResponseDTO>} */
  update: (id, data) => axiosInstance.put(`/students/${id}`, data),

  // ── §7.8  PATCH /students/:id/status  ───────────────────────────────────
  /** @returns {Promise<StudentResponseDTO>} */
  updateStatus: (id, status) => axiosInstance.patch(`/students/${id}/status`, { status }),

  // ── §7.7  DELETE /students/:id  ─────────────────────────────────────────
  /** @returns {Promise<null>} */
  remove: (id) => axiosInstance.delete(`/students/${id}`),

  // ── §7.9  POST /students/:id/profile-image  ─────────────────────────────
  /**
   * Upload a profile image for a student.
   * @param {number} id
   * @param {File}   file
   * @returns {Promise<StudentResponseDTO>}
   */
  uploadImage: (id, file) => {
    const form = new FormData()
    form.append('file', file)
    return axiosInstance.post(`/students/${id}/profile-image`, form, {
      // Let the browser set multipart boundary automatically by omitting Content-Type
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  // ── §7.10 DELETE /students/:id/profile-image  ───────────────────────────
  /** @returns {Promise<null>} */
  removeImage: (id) => axiosInstance.delete(`/students/${id}/profile-image`),

  // ── §7.11 GET /students/export  ─────────────────────────────────────────
  /**
   * Download a CSV export of filtered students.
   * Returns the raw Axios response (blob) — the success interceptor bypasses
   * unwrapping when responseType === 'blob'.
   * @param {object} params - same filter params as getAll
   * @returns {Promise<AxiosResponse<Blob>>}
   */
  exportCsv: (params = {}) => {
    const clean = Object.fromEntries(
      Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined),
    )
    return axiosInstance.get('/students/export', { params: clean, responseType: 'blob' })
  },
}

export default studentApi
