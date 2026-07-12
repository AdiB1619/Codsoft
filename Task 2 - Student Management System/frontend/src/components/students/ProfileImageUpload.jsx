import { useState, useRef, useEffect } from 'react'
import { Camera, X, Loader2 } from 'lucide-react'
import { validateProfileImage } from '../../utils/validators'
import studentApi from '../../api/studentApi'
import useToast from '../../hooks/useToast'
import { formatFullName } from '../../utils/formatters'

/**
 * Handles profile image upload/preview/remove.
 * Works in two modes:
 *  - 'form': Defers actual API upload to the parent form (passes File via onChange).
 *  - 'instant': Immediately uploads via studentApi using studentId.
 */
function ProfileImageUpload({
  currentImageUrl = null,
  studentId,
  firstName = 'Unknown',
  lastName = '',
  onChange, // Form mode: (file: File | 'REMOVE' | null) => void
  onSuccess, // Instant mode: (newUrl: string | null) => void
  mode = 'form',
}) {
  const [previewUrl, setPreviewUrl] = useState(currentImageUrl)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  
  const fileInputRef = useRef(null)
  const { showToast } = useToast()

  // Keep preview in sync with external changes (e.g. when fetching Edit data)
  useEffect(() => {
    if (mode === 'instant' || !previewUrl || previewUrl.startsWith('http')) {
      setPreviewUrl(currentImageUrl)
    }
  }, [currentImageUrl, mode])

  const defaultAvatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(formatFullName(firstName, lastName))}&background=f1f5f9&color=64748b&size=128`
  const activeImageUrl = previewUrl || defaultAvatarUrl

  const handleFileChange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    // 1. Client-side validation per Section 10
    const validationError = validateProfileImage(file)
    if (validationError) {
      setError(validationError)
      if (mode === 'instant') showToast(validationError, 'error')
      if (fileInputRef.current) fileInputRef.current.value = ''
      return
    }

    setError(null)
    
    // 2. Generate local preview
    const localUrl = URL.createObjectURL(file)

    // 3. Handle based on mode
    if (mode === 'form') {
      setPreviewUrl(localUrl)
      if (onChange) onChange(file)
    } else if (mode === 'instant') {
      if (!studentId) return
      setLoading(true)
      try {
        const res = await studentApi.uploadImage(studentId, file)
        showToast('Profile image updated successfully.', 'success')
        setPreviewUrl(res.profileImageUrl)
        if (onSuccess) onSuccess(res.profileImageUrl)
      } catch (err) {
        showToast(err.message ?? 'Failed to upload image.', 'error')
      } finally {
        setLoading(false)
        if (fileInputRef.current) fileInputRef.current.value = ''
      }
    }
  }

  const handleRemove = async () => {
    setError(null)
    if (mode === 'form') {
      setPreviewUrl(null)
      if (onChange) onChange('REMOVE')
      if (fileInputRef.current) fileInputRef.current.value = ''
    } else if (mode === 'instant') {
      if (!studentId) return
      setLoading(true)
      try {
        await studentApi.removeImage(studentId)
        showToast('Profile image removed successfully.', 'success')
        setPreviewUrl(null)
        if (onSuccess) onSuccess(null)
      } catch (err) {
        showToast(err.message ?? 'Failed to remove image.', 'error')
      } finally {
        setLoading(false)
      }
    }
  }

  return (
    <div className="flex flex-col sm:flex-row items-center sm:items-start gap-4">
      {/* ── Avatar display ── */}
      <div className="relative group">
        <div className="w-24 h-24 shrink-0 rounded-full bg-slate-100 border-4 border-white shadow-sm overflow-hidden flex items-center justify-center">
          {loading ? (
            <Loader2 size={24} className="text-slate-400 animate-spin" />
          ) : (
            <img
              src={activeImageUrl}
              alt="Profile avatar"
              className="w-full h-full object-cover"
            />
          )}
        </div>
        
        {/* Hover overlay for quick edit (Optional, makes UI nice) */}
        {!loading && (
          <label 
            htmlFor={`file-upload-${studentId || 'new'}`}
            className="absolute inset-0 bg-black/40 text-white rounded-full flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity duration-200"
            title="Upload new photo"
          >
            <Camera size={20} />
          </label>
        )}
      </div>

      {/* ── Controls ── */}
      <div className="flex flex-col gap-2 flex-1 w-full sm:w-auto">
        <div className="flex items-center gap-2 justify-center sm:justify-start">
          <label
            htmlFor={`file-upload-${studentId || 'new'}`}
            className="inline-flex items-center justify-center gap-2 px-3 py-1.5 text-sm font-medium rounded-md bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer transition-colors focus-within:ring-2 focus-within:ring-primary/20 focus-within:border-primary disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Camera size={14} />
            <span>Upload Photo</span>
            <input
              id={`file-upload-${studentId || 'new'}`}
              type="file"
              accept="image/jpeg,image/png"
              className="sr-only"
              onChange={handleFileChange}
              ref={fileInputRef}
              disabled={loading}
            />
          </label>

          {(previewUrl || (mode === 'instant' && currentImageUrl)) && (
            <button
              type="button"
              onClick={handleRemove}
              disabled={loading}
              className="inline-flex items-center justify-center gap-1.5 px-3 py-1.5 text-sm font-medium rounded-md bg-white border border-slate-300 text-danger hover:bg-danger/5 transition-colors focus-ring disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <X size={14} />
              <span className="hidden sm:inline">Remove</span>
            </button>
          )}
        </div>
        
        {/* Field-level error specifically for the upload */}
        {error && (
          <p className="text-xs text-danger font-medium text-center sm:text-left animate-[fadeScale_200ms_ease-out]">
            {error}
          </p>
        )}
        
        <p className="text-xs text-slate-400 text-center sm:text-left mt-1">
          JPG or PNG only. Maximum file size 2MB.
        </p>
      </div>
    </div>
  )
}

export default ProfileImageUpload
