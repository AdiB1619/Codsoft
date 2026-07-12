import axiosInstance from './axiosInstance'

/**
 * courseApi — all REST calls to /api/v1/courses.
 *
 * Covers §7.12 (GET /courses) and §7.13 (POST /courses) plus full CRUD.
 * Every function resolves to the already-unwrapped payload.
 */
const courseApi = {
  // ── §7.12 GET /courses  ──────────────────────────────────────────────────
  /** @returns {Promise<CourseResponseDTO[]>} */
  getAll: () => axiosInstance.get('/courses'),

  // ── GET /courses/:id  ────────────────────────────────────────────────────
  /** @returns {Promise<CourseResponseDTO>} */
  getById: (id) => axiosInstance.get(`/courses/${id}`),

  // ── §7.13 POST /courses  ─────────────────────────────────────────────────
  /** @returns {Promise<CourseResponseDTO>} */
  create: (data) => axiosInstance.post('/courses', data),

  // ── PUT /courses/:id  ────────────────────────────────────────────────────
  /** @returns {Promise<CourseResponseDTO>} */
  update: (id, data) => axiosInstance.put(`/courses/${id}`, data),

  // ── DELETE /courses/:id  ─────────────────────────────────────────────────
  /** @returns {Promise<null>} */
  remove: (id) => axiosInstance.delete(`/courses/${id}`),
}

export default courseApi
