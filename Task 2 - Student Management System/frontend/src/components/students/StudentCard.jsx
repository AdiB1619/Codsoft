import { Pencil, Trash2, Mail, Phone, MapPin, Calendar, BookOpen, Hash, GraduationCap, Clock, User } from 'lucide-react'
import Badge from '../common/Badge'
import Button from '../common/Button'
import ProfileImageUpload from './ProfileImageUpload'
import { formatFullName, formatDate } from '../../utils/formatters'

/**
 * Presentational component for displaying a student's full profile.
 * 
 * @param {Object} student - The StudentResponseDTO object
 * @param {Function} onEdit - Callback for the edit action
 * @param {Function} onDelete - Callback for the delete action
 * @param {Function} onImageUpdate - Callback when image is updated instantly
 */
function StudentCard({ student, onEdit, onDelete, onImageUpdate }) {
  if (!student) return null

  const fullName = formatFullName(student.firstName, student.lastName)

  // Detail item helper
  const DetailItem = ({ icon: Icon, label, value }) => (
    <div className="flex items-start gap-3">
      <div className="p-2 bg-slate-50 rounded text-slate-400 shrink-0">
        <Icon size={18} aria-hidden="true" />
      </div>
      <div>
        <dt className="text-xs font-medium text-slate-500 uppercase tracking-wider mb-0.5">{label}</dt>
        <dd className="text-sm font-medium text-slate-800 break-words">{value || '—'}</dd>
      </div>
    </div>
  )

  return (
    <div className="bg-white rounded-lg border border-slate-200 shadow-sm overflow-hidden">
      {/* ── Profile Header ──────────────────────────────────────────────── */}
      <div className="p-6 sm:p-8 border-b border-slate-100 flex flex-col sm:flex-row items-center sm:items-start gap-6 text-center sm:text-left">
        <ProfileImageUpload
          mode="instant"
          currentImageUrl={student.profileImageUrl}
          studentId={student.id}
          firstName={student.firstName}
          lastName={student.lastName}
          onSuccess={onImageUpdate}
        />
        
        <div className="flex-1">
          <h2 className="font-heading text-2xl font-semibold text-primary mb-1">
            {fullName}
          </h2>
          <div className="flex flex-col sm:flex-row items-center sm:items-center gap-3 text-sm text-slate-500 mb-4">
            <span className="font-mono bg-slate-100 px-2 py-0.5 rounded text-slate-600">
              {student.rollNumber}
            </span>
            <span className="hidden sm:inline text-slate-300">•</span>
            <Badge status={student.status} />
          </div>
        </div>

        <div className="flex items-center gap-3 w-full sm:w-auto justify-center sm:justify-start mt-4 sm:mt-0">
          <Button
            variant="outline"
            onClick={onEdit}
            className="flex-1 sm:flex-none"
          >
            <Pencil size={16} className="mr-2" />
            Edit
          </Button>
          <Button
            variant="danger"
            onClick={onDelete}
            className="flex-1 sm:flex-none"
          >
            <Trash2 size={16} className="mr-2" />
            Delete
          </Button>
        </div>
      </div>

      {/* ── Detail Sections ─────────────────────────────────────────────── */}
      <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-x-12 gap-y-10">
        
        {/* Personal Info */}
        <div>
          <h3 className="font-heading text-lg font-medium text-slate-800 border-b border-slate-100 pb-2 mb-6">
            Personal Information
          </h3>
          <dl className="space-y-5">
            <DetailItem icon={Mail} label="Email Address" value={
              <a href={`mailto:${student.email}`} className="text-primary hover:underline">{student.email}</a>
            } />
            <DetailItem icon={Phone} label="Phone Number" value={student.phoneNumber} />
            <DetailItem icon={Calendar} label="Date of Birth" value={formatDate(student.dateOfBirth)} />
            <DetailItem icon={User} label="Gender" value={
              student.gender ? student.gender.charAt(0) + student.gender.slice(1).toLowerCase() : '—'
            } />
            <DetailItem icon={MapPin} label="Address" value={student.address} />
          </dl>
        </div>

        {/* Academic Info */}
        <div>
          <h3 className="font-heading text-lg font-medium text-slate-800 border-b border-slate-100 pb-2 mb-6">
            Academic Information
          </h3>
          <dl className="space-y-5">
            <DetailItem icon={BookOpen} label="Course" value={student.course?.courseName || '—'} />
            <DetailItem icon={Hash} label="Course Code" value={student.course?.courseCode || '—'} />
            <DetailItem icon={Clock} label="Enrollment Date" value={formatDate(student.enrollmentDate)} />
            <DetailItem icon={GraduationCap} label="Current Grade" value={
              student.grade !== null && student.grade !== undefined 
                ? `${student.grade}%` 
                : 'Not graded yet'
            } />
          </dl>
        </div>

      </div>
    </div>
  )
}

export default StudentCard
