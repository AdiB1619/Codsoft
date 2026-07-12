import ConfirmModal from '../common/ConfirmModal'
import { formatFullName } from '../../utils/formatters'

/**
 * A thin wrapper around ConfirmModal pre-configured for deleting a student.
 * Ensures consistent messaging and styling across the List and Details pages.
 *
 * @param {Object} student - The student to delete, or null if modal is closed
 * @param {Function} onClose - Called when the modal is dismissed
 * @param {Function} onConfirm - Called when the deletion is confirmed
 * @param {boolean} isDeleting - True if the delete API request is in flight
 */
function DeleteConfirmModal({ student, onClose, onConfirm, isDeleting }) {
  const message = student 
    ? `Are you sure you want to delete ${formatFullName(student.firstName, student.lastName)} (${student.rollNumber})? This action cannot be undone.`
    : ''

  return (
    <ConfirmModal
      isOpen={!!student}
      onClose={onClose}
      onConfirm={onConfirm}
      loading={isDeleting}
      title="Delete Student"
      message={message}
      confirmLabel="Delete"
      confirmVariant="danger"
    />
  )
}

export default DeleteConfirmModal
