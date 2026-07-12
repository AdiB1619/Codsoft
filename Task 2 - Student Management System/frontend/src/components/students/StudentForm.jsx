import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../common/Button'
import courseApi from '../../api/courseApi'
import { validateStudentForm } from '../../utils/validators'
import { STUDENT_STATUSES } from '../../utils/constants'
import ProfileImageUpload from './ProfileImageUpload'

const DEFAULT_VALUES = {
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  dateOfBirth: '',
  gender: '',
  courseId: '',
  rollNumber: '',
  enrollmentDate: '',
  status: 'ACTIVE',
  address: '',
  grade: '',
}

/**
 * Reusable form for creating and editing students.
 * Performs client-side validation against SDD Section 10 rules before submission.
 *
 * @param {Partial<typeof DEFAULT_VALUES>} initialValues
 * @param {(values, profileImageFile) => Promise<void>} onSubmit
 * @param {boolean} isSubmitting
 * @param {object} serverErrors - field-level error mapping from the backend (e.g. { rollNumber: "Roll number already exists" })
 */
function StudentForm({ initialValues = {}, onSubmit, isSubmitting = false, serverErrors = {} }) {
  const navigate = useNavigate()
  
  // ── State ────────────────────────────────────────────────────────────────
  const [values, setValues] = useState({ ...DEFAULT_VALUES, ...initialValues })
  const [profileImageFile, setProfileImageFile] = useState(null) // File | 'REMOVE' | null
  const [touched, setTouched] = useState({})
  const [clientErrors, setClientErrors] = useState({})
  const [courses, setCourses] = useState([])

  // ── Fetch courses for dropdown ───────────────────────────────────────────
  useEffect(() => {
    courseApi.getAll()
      .then(data => setCourses(Array.isArray(data) ? data : []))
      .catch(err => console.error('Failed to load courses', err))
  }, [])

  // ── Handlers ─────────────────────────────────────────────────────────────
  const handleChange = (e) => {
    const { name, value } = e.target
    setValues(prev => ({ ...prev, [name]: value }))
    // Clear the specific error when user types
    if (clientErrors[name] || serverErrors[name]) {
      setClientErrors(prev => ({ ...prev, [name]: null }))
    }
  }

  const handleBlur = (e) => {
    const { name } = e.target
    setTouched(prev => ({ ...prev, [name]: true }))
    
    // Validate the entire form but we only care about this field for immediate feedback
    const errs = validateStudentForm({ ...values, [name]: e.target.value })
    if (errs[name]) {
      setClientErrors(prev => ({ ...prev, [name]: errs[name] }))
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    // Mark all fields touched
    const allTouched = Object.keys(DEFAULT_VALUES).reduce((acc, key) => {
      acc[key] = true; return acc
    }, {})
    setTouched(allTouched)

    // Validate all
    const errors = validateStudentForm(values)
    setClientErrors(errors)

    // Submit if valid
    if (Object.keys(errors).length === 0) {
      onSubmit(values, profileImageFile)
    }
  }

  // Helper to get the active error for a field (server errors take precedence)
  const getError = (name) => {
    if (serverErrors[name]) return serverErrors[name]
    if (touched[name] && clientErrors[name]) return clientErrors[name]
    return null
  }

  // Input wrapper for consistent styling and error messages
  const FormGroup = ({ label, name, type = 'text', required = false, children, placeholder }) => {
    const error = getError(name)
    const inputClasses = `w-full px-3 py-2 bg-white border rounded-md text-sm transition-colors duration-hover focus-ring
      ${error ? 'border-danger focus:border-danger focus:ring-danger/20' : 'border-slate-300 focus:border-primary'}`

    return (
      <div className="flex flex-col gap-1.5">
        <label htmlFor={name} className="text-sm font-medium text-slate-700">
          {label} {required && <span className="text-danger" aria-hidden="true">*</span>}
        </label>
        {children ? (
          children(inputClasses)
        ) : (
          <input
            id={name}
            name={name}
            type={type}
            value={values[name]}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder={placeholder}
            className={inputClasses}
            aria-invalid={!!error}
            aria-describedby={error ? `${name}-error` : undefined}
          />
        )}
        {error && (
          <p id={`${name}-error`} className="text-xs text-danger font-medium animate-[fadeScale_200ms_ease-out]">
            {error}
          </p>
        )}
      </div>
    )
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="First Name" name="firstName" required />
        <FormGroup label="Last Name" name="lastName" required />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="Email" name="email" type="email" required />
        <FormGroup label="Phone Number" name="phoneNumber" type="tel" required placeholder="+1234567890" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="Date of Birth" name="dateOfBirth" type="date" required />
        <FormGroup label="Gender" name="gender" required>
          {(classes) => (
            <select
              id="gender"
              name="gender"
              value={values.gender}
              onChange={handleChange}
              onBlur={handleBlur}
              className={`${classes} appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20width%3D%2224%22%20height%3D%2224%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22%2364748b%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpolyline%20points%3D%226%209%2012%2015%2018%209%22%3E%3C%2Fpolyline%3E%3C%2Fsvg%3E')] bg-no-repeat bg-[position:right_0.5rem_center] bg-[length:1.2em_1.2em] pr-8`}
              aria-invalid={!!getError('gender')}
              aria-describedby={getError('gender') ? `gender-error` : undefined}
            >
              <option value="" disabled>Select gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          )}
        </FormGroup>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="Course" name="courseId" required>
          {(classes) => (
            <select
              id="courseId"
              name="courseId"
              value={values.courseId}
              onChange={handleChange}
              onBlur={handleBlur}
              className={`${classes} appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20width%3D%2224%22%20height%3D%2224%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22%2364748b%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpolyline%20points%3D%226%209%2012%2015%2018%209%22%3E%3C%2Fpolyline%3E%3C%2Fsvg%3E')] bg-no-repeat bg-[position:right_0.5rem_center] bg-[length:1.2em_1.2em] pr-8`}
              aria-invalid={!!getError('courseId')}
              aria-describedby={getError('courseId') ? `courseId-error` : undefined}
            >
              <option value="" disabled>Select a course</option>
              {courses.map(c => (
                <option key={c.id} value={c.id}>{c.courseName}</option>
              ))}
            </select>
          )}
        </FormGroup>
        <FormGroup label="Roll Number" name="rollNumber" required placeholder="e.g. CS2023045" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="Enrollment Date" name="enrollmentDate" type="date" required />
        <FormGroup label="Status" name="status" required>
          {(classes) => (
            <select
              id="status"
              name="status"
              value={values.status}
              onChange={handleChange}
              onBlur={handleBlur}
              className={`${classes} appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20width%3D%2224%22%20height%3D%2224%22%20viewBox%3D%220%200%2024%2024%22%20fill%3D%22none%22%20stroke%3D%22%2364748b%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpolyline%20points%3D%226%209%2012%2015%2018%209%22%3E%3C%2Fpolyline%3E%3C%2Fsvg%3E')] bg-no-repeat bg-[position:right_0.5rem_center] bg-[length:1.2em_1.2em] pr-8`}
              aria-invalid={!!getError('status')}
              aria-describedby={getError('status') ? `status-error` : undefined}
            >
              {STUDENT_STATUSES.map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          )}
        </FormGroup>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="md:col-span-2">
          <FormGroup label="Address" name="address" required>
            {(classes) => (
              <textarea
                id="address"
                name="address"
                rows="3"
                value={values.address}
                onChange={handleChange}
                onBlur={handleBlur}
                className={classes}
                aria-invalid={!!getError('address')}
                aria-describedby={getError('address') ? `address-error` : undefined}
              />
            )}
          </FormGroup>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <FormGroup label="Grade (%)" name="grade" type="number" placeholder="0 - 100 (Optional)" />
        <div className="flex flex-col gap-1.5">
          <label className="text-sm font-medium text-slate-700">Profile Image</label>
          <div className="mt-1">
            <ProfileImageUpload
              mode="form"
              currentImageUrl={initialValues.profileImageUrl}
              firstName={values.firstName}
              lastName={values.lastName}
              onChange={setProfileImageFile}
            />
          </div>
        </div>
      </div>

      <div className="pt-4 flex justify-end gap-3 border-t border-slate-100">
        <Button
          type="button"
          variant="ghost"
          onClick={() => navigate(-1)}
          disabled={isSubmitting}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          loading={isSubmitting}
        >
          Save Student
        </Button>
      </div>
    </form>
  )
}

export default StudentForm
