/**
 * Application-wide constants shared across the frontend.
 * Mirror of AppConstants.java on the backend.
 */
export const STUDENT_STATUSES = ['ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED']

export const DEFAULT_PAGE_SIZE = 10

export const SORT_FIELDS = [
  { label: 'ID',             value: 'id' },
  { label: 'First Name',     value: 'firstName' },
  { label: 'Last Name',      value: 'lastName' },
  { label: 'Roll Number',    value: 'rollNumber' },
  { label: 'Enrollment Date', value: 'enrollmentDate' },
]
