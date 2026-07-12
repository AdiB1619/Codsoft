import { useState, useEffect } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, User } from 'lucide-react'

import studentApi from '../api/studentApi'
import useToast from '../hooks/useToast'
import Loader from '../components/common/Loader'
import EmptyState from '../components/common/EmptyState'
import StudentCard from '../components/students/StudentCard'
import DeleteConfirmModal from '../components/students/DeleteConfirmModal'

/**
 * StudentDetailsPage — Section 8.6
 * Displays the full read-only profile of a student.
 */
function StudentDetailsPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { showToast } = useToast()

  const [student, setStudent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)

  // Fetch student details
  useEffect(() => {
    studentApi.getById(id)
      .then(data => {
        setStudent(data)
        setLoading(false)
      })
      .catch(err => {
        setNotFound(true)
        setLoading(false)
        if (err.status !== 404) {
          showToast(err.message ?? 'Failed to load student details.', 'error')
        }
      })
  }, [id, showToast])

  // ── Modal state ──────────────────────────────────────────────────────────
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  // Handlers for the StudentCard action buttons
  const handleEdit = () => {
    navigate(`/students/${id}/edit`)
  }

  const handleDeleteRequest = () => {
    setShowDeleteModal(true)
  }

  const handleDeleteConfirm = async () => {
    setIsDeleting(true)
    try {
      await studentApi.remove(id)
      showToast('Student deleted successfully.', 'success')
      navigate('/students')
    } catch (err) {
      if (err.status === 404 || err.message?.toLowerCase().includes('not found')) {
        showToast('Student has already been deleted.', 'error')
        navigate('/students')
      } else {
        showToast(err.message ?? 'Failed to delete student.', 'error')
      }
    } finally {
      setIsDeleting(false)
      setShowDeleteModal(false)
    }
  }

  // ── Render ───────────────────────────────────────────────────────────────

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

  return (
    <div>
      <button
        onClick={() => navigate('/students')}
        className="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-primary
          mb-4 transition-colors duration-hover focus-ring rounded"
      >
        <ArrowLeft size={15} aria-hidden="true" />
        Back to Students
      </button>

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h1 className="font-heading text-2xl font-semibold text-primary flex items-center gap-2">
            <User size={22} aria-hidden="true" />
            Student Details
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Viewing record for <span className="font-mono">{student.rollNumber}</span>
          </p>
        </div>
      </div>

      <StudentCard
        student={student}
        onEdit={handleEdit}
        onDelete={handleDeleteRequest}
        onImageUpdate={(newUrl) => setStudent({ ...student, profileImageUrl: newUrl })}
      />

      <DeleteConfirmModal
        student={showDeleteModal ? student : null}
        onClose={() => !isDeleting && setShowDeleteModal(false)}
        onConfirm={handleDeleteConfirm}
        isDeleting={isDeleting}
      />
    </div>
  )
}

export default StudentDetailsPage
