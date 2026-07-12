import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, UserPlus } from 'lucide-react'

import studentApi from '../api/studentApi'
import useToast from '../hooks/useToast'
import StudentForm from '../components/students/StudentForm'

/**
 * AddStudentPage — implements Section 8.5 wireframe for creating a new student.
 */
function AddStudentPage() {
  const navigate = useNavigate()
  const { showToast } = useToast()
  
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [serverErrors, setServerErrors] = useState({})

  const handleSubmit = async (values, profileImageFile) => {
    setIsSubmitting(true)
    setServerErrors({})

    try {
      // 1. Create the student JSON payload
      const payload = {
        ...values,
        courseId: parseInt(values.courseId, 10),
        grade: values.grade ? parseFloat(values.grade) : null,
      }
      
      const newStudent = await studentApi.create(payload)

      // 2. If an image was selected, upload it in a separate request
      if (profileImageFile && profileImageFile !== 'REMOVE') {
        try {
          await studentApi.uploadImage(newStudent.id, profileImageFile)
          showToast('Student created and image uploaded successfully.', 'success')
        } catch (uploadErr) {
          showToast('Student created, but image upload failed. ' + (uploadErr.message || ''), 'warning')
        }
      } else {
        showToast('Student created successfully.', 'success')
      }

      // Navigate to details page of the new student
      navigate(`/students/${newStudent.id}`)
    } catch (err) {
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        const errorsMap = {}
        err.fieldErrors.forEach(fe => { errorsMap[fe.field] = fe.message })
        setServerErrors(errorsMap)
        showToast('Please correct the highlighted fields.', 'error')
      } else {
        showToast(err.message ?? 'Failed to create student.', 'error')
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

  return (
    <div>
      {/* ── Breadcrumb / back link ──────────────────────────────────────── */}
      <button
        onClick={() => navigate('/students')}
        className="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-primary
          mb-4 transition-colors duration-hover focus-ring rounded"
      >
        <ArrowLeft size={15} aria-hidden="true" />
        Back to Students
      </button>

      {/* ── Page Header ─────────────────────────────────────────────────── */}
      <div className="mb-6">
        <h1 className="font-heading text-2xl font-semibold text-primary flex items-center gap-2">
          <UserPlus size={22} aria-hidden="true" />
          Add New Student
        </h1>
        <p className="text-sm text-slate-500 mt-1">Fill in the details to create a new student record.</p>
      </div>

      {/* ── Form Container ──────────────────────────────────────────────── */}
      <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-6 sm:p-8">
        <StudentForm
          onSubmit={handleSubmit}
          isSubmitting={isSubmitting}
          serverErrors={serverErrors}
        />
      </div>
    </div>
  )
}

export default AddStudentPage
