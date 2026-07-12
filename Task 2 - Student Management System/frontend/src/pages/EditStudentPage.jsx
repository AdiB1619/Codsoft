import { useState, useEffect } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import { ArrowLeft, Pencil } from 'lucide-react'

import studentApi from '../api/studentApi'
import useToast from '../hooks/useToast'
import StudentForm from '../components/students/StudentForm'
import Loader from '../components/common/Loader'
import EmptyState from '../components/common/EmptyState'
import { formatFullName } from '../utils/formatters'

/**
 * EditStudentPage — Section 8.5 wireframe for updating a student.
 * Reuses StudentForm with pre-populated initialValues.
 */
function EditStudentPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { showToast } = useToast()

  // ── Fetch state ────────────────────────────────────────────────────────
  const [student, setStudent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    studentApi.getById(id)
      .then(data => {
        setStudent(data)
        setLoading(false)
      })
      .catch(err => {
        // Fallback to friendly empty state on 404 or invalid ID
        setNotFound(true)
        setLoading(false)
        if (err.status !== 404) {
          showToast(err.message ?? 'Failed to load student details.', 'error')
        }
      })
  }, [id, showToast])

  // ── Submit state ───────────────────────────────────────────────────────
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [serverErrors, setServerErrors] = useState({})

  const handleSubmit = async (values, profileImageFile) => {
    setIsSubmitting(true)
    setServerErrors({})
    try {
      const payload = {
        ...values,
        courseId: parseInt(values.courseId, 10),
        grade: values.grade ? parseFloat(values.grade) : null,
      }

      await studentApi.update(id, payload)

      // Handle image updates
      if (profileImageFile === 'REMOVE') {
        try {
          await studentApi.removeImage(id)
        } catch (removeErr) {
          showToast('Student updated, but failed to remove image. ' + (removeErr.message || ''), 'warning')
        }
      } else if (profileImageFile) {
        try {
          await studentApi.uploadImage(id, profileImageFile)
        } catch (uploadErr) {
          showToast('Student updated, but image upload failed. ' + (uploadErr.message || ''), 'warning')
        }
      }

      showToast('Student updated successfully.', 'success')
      navigate(`/students/${id}`)
    } catch (err) {
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        const errorsMap = {}
        err.fieldErrors.forEach(fe => { errorsMap[fe.field] = fe.message })
        setServerErrors(errorsMap)
        showToast('Please correct the highlighted fields.', 'error')
      } else {
        showToast(err.message ?? 'Failed to update student.', 'error')
        if (err.message?.toLowerCase().includes('roll number')) {
          setServerErrors({ rollNumber: err.message })
        } else if (err.message?.toLowerCase().includes('email')) {
          setServerErrors({ email: err.message })
        }
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  // ── Rendering ──────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-16">
        <Loader label="Loading student details…" />
      </div>
    )
  }

  if (notFound) {
    return (
      <div className="bg-white rounded-lg border border-slate-200 shadow-sm">
        <EmptyState
          title="Student Not Found"
          description={`No student record exists with ID ${id}.`}
          action={
            <Link
              to="/students"
              className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-primary
                text-white text-sm font-medium hover:bg-primary-hover
                transition-colors duration-hover focus-ring"
            >
              Back to Students
            </Link>
          }
        />
      </div>
    )
  }

  // Map API response to the flat form values expected by StudentForm
  const initialValues = {
    firstName: student.firstName || '',
    lastName: student.lastName || '',
    email: student.email || '',
    phoneNumber: student.phoneNumber || '',
    dateOfBirth: student.dateOfBirth?.slice(0, 10) || '',
    gender: student.gender || '',
    courseId: student.course?.id || '',
    rollNumber: student.rollNumber || '',
    enrollmentDate: student.enrollmentDate?.slice(0, 10) || '',
    status: student.status || 'ACTIVE',
    address: student.address || '',
    grade: student.grade ?? '',
  }

  return (
    <div>
      <button
        onClick={() => navigate(`/students/${id}`)}
        className="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-primary
          mb-4 transition-colors duration-hover focus-ring rounded"
      >
        <ArrowLeft size={15} aria-hidden="true" />
        Back to Student Details
      </button>

      <div className="mb-6">
        <h1 className="font-heading text-2xl font-semibold text-primary flex items-center gap-2">
          <Pencil size={22} aria-hidden="true" />
          Edit Student
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Updating record for <span className="font-medium text-slate-700">{formatFullName(student.firstName, student.lastName)}</span> ({student.rollNumber}).
        </p>
      </div>

      <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-6 sm:p-8">
        <StudentForm
          initialValues={initialValues}
          onSubmit={handleSubmit}
          isSubmitting={isSubmitting}
          serverErrors={serverErrors}
        />
      </div>
    </div>
  )
}

export default EditStudentPage
