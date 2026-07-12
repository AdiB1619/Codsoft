import axios from 'axios'

/**
 * Central Axios instance — the single point of truth for all HTTP communication.
 *
 * Architecture rules enforced here:
 *  - Base URL comes from VITE_API_BASE_URL (never hardcoded in components)
 *  - Success interceptor unwraps ApiResponse<T> so callers receive T directly
 *  - Error interceptor maps backend ErrorResponse → ApiError JS object
 *
 * Only this file and the api/ modules import `axios`.
 * Components and hooks must never import axios directly.
 */
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15_000, // 15 seconds
})

// ── Success Interceptor ────────────────────────────────────────────────────────
// The Spring Boot backend wraps every response in:
//   { success: true, message: "...", data: <T>, timestamp: "..." }
//
// This interceptor unwraps that envelope so every API call resolves directly
// to the payload. Blob responses (CSV export) bypass unwrapping.
axiosInstance.interceptors.response.use((response) => {
  // Skip unwrapping for blob downloads — the caller handles raw data there
  if (response.config.responseType === 'blob') {
    return response
  }

  // Unwrap ApiResponse envelope: return response.data.data (the real payload)
  // If the backend omits the wrapper (e.g. 204 No Content), fall through to null
  return response.data?.data ?? response.data ?? null
})

// ── Error Interceptor ──────────────────────────────────────────────────────────
// Maps backend ErrorResponse JSON into a consistent ApiError shape:
//   { message, status, fieldErrors, path }
// Network errors and non-JSON responses are also normalised.
axiosInstance.interceptors.response.use(
  undefined, // no-op for success — handled above
  (error) => {
    const responseData = error.response?.data

    // Build a rich error object from the Spring Boot ErrorResponse DTO
    const apiError = {
      message:     responseData?.message ?? error.message ?? 'An unexpected error occurred.',
      status:      error.response?.status ?? 0,
      fieldErrors: responseData?.fieldErrors ?? [],   // ValidationException details
      path:        responseData?.path ?? '',
    }

    // Log internally for debugging; never expose raw stack trace to users
    if (import.meta.env.DEV) {
      console.error('[API Error]', apiError)
    }

    return Promise.reject(apiError)
  },
)

export default axiosInstance
