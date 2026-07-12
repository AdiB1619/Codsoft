/**
 * Client-side validators — mirrors the backend Bean Validation rules (Section 10)
 * so that the form can give instant feedback before a network round-trip.
 */

export function validateFirstName(value) {
  if (!value || value.trim() === '') return 'First name is required.'
  if (!/^[A-Za-z\s'-]{2,50}$/.test(value)) return "First name must be 2-50 characters and contain only letters"
  return null
}

export function validateLastName(value) {
  if (!value || value.trim() === '') return 'Last name is required.'
  if (!/^[A-Za-z\s'-]{2,50}$/.test(value)) return "Last name must be 2-50 characters and contain only letters"
  return null
}

export function validateEmail(email) {
  if (!email || email.trim() === '') return 'Email is required.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return 'Enter a valid email address'
  return null
}

export function validatePhoneNumber(phone) {
  if (!phone || phone.trim() === '') return 'Phone number is required.'
  if (!/^\+?[0-9]{10,13}$/.test(phone)) return 'Enter a valid phone number'
  return null
}

export function validateDateOfBirth(dobStr) {
  if (!dobStr) return 'Date of birth is required.'
  const dob = new Date(dobStr)
  if (isNaN(dob.getTime())) return 'Enter a valid date'
  
  const ageDifMs = Date.now() - dob.getTime()
  const ageDate = new Date(ageDifMs)
  const age = Math.abs(ageDate.getUTCFullYear() - 1970)
  
  if (age < 10) return 'Student must be at least 10 years old'
  return null
}

export function validateAddress(address) {
  if (!address || address.trim() === '') return 'Address is required.'
  const len = address.trim().length
  if (len < 5 || len > 200) return 'Address must be between 5 and 200 characters'
  return null
}

export function validateRollNumber(rollNumber) {
  if (!rollNumber || rollNumber.trim() === '') return 'Roll number is required.'
  if (!/^[A-Z]{2,4}[0-9]{3,6}$/.test(rollNumber)) {
    return 'Roll number format is invalid'
  }
  return null
}

export function validateCourseId(courseId) {
  if (!courseId) return 'Select a valid course'
  return null
}

export function validateEnrollmentDate(dateStr) {
  if (!dateStr) return 'Enrollment date is required.'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return 'Enter a valid date'
  
  // Strip time from both dates to compare purely on date
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const compareDate = new Date(d)
  compareDate.setHours(0, 0, 0, 0)

  if (compareDate > today) return 'Enrollment date cannot be in the future'
  return null
}

export function validateGender(gender) {
  if (!gender) return 'Select a gender'
  if (!['MALE', 'FEMALE', 'OTHER'].includes(gender)) return 'Select a gender'
  return null
}

export function validateStatus(status) {
  if (!status) return 'Invalid status value'
  if (!['ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED'].includes(status)) return 'Invalid status value'
  return null
}

export function validateGrade(grade) {
  if (grade === '' || grade === null || grade === undefined) return null // optional
  const num = parseFloat(grade)
  if (isNaN(num) || num < 0 || num > 100) return 'Grade must be between 0 and 100'
  return null
}

export function validateProfileImage(file) {
  if (!file) return null
  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    return 'Only JPG or PNG images are allowed'
  }
  if (file.size > 2 * 1024 * 1024) {
    return 'Image must be smaller than 2MB'
  }
  return null
}

// Helper to run all validators
export function validateStudentForm(values) {
  const errors = {}
  
  const fn = validateFirstName(values.firstName)
  if (fn) errors.firstName = fn

  const ln = validateLastName(values.lastName)
  if (ln) errors.lastName = ln

  const em = validateEmail(values.email)
  if (em) errors.email = em

  const pn = validatePhoneNumber(values.phoneNumber)
  if (pn) errors.phoneNumber = pn

  const dob = validateDateOfBirth(values.dateOfBirth)
  if (dob) errors.dateOfBirth = dob

  const gen = validateGender(values.gender)
  if (gen) errors.gender = gen

  const addr = validateAddress(values.address)
  if (addr) errors.address = addr

  const rn = validateRollNumber(values.rollNumber)
  if (rn) errors.rollNumber = rn

  const cid = validateCourseId(values.courseId)
  if (cid) errors.courseId = cid

  const ed = validateEnrollmentDate(values.enrollmentDate)
  if (ed) errors.enrollmentDate = ed

  const st = validateStatus(values.status)
  if (st) errors.status = st

  const gr = validateGrade(values.grade)
  if (gr) errors.grade = gr

  return errors
}
