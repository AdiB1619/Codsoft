package com.codsoft.sms.service.impl;

import com.codsoft.sms.dto.request.StudentRequestDTO;
import com.codsoft.sms.dto.response.PagedResponse;
import com.codsoft.sms.dto.response.StudentResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.entity.Student;
import com.codsoft.sms.entity.enums.StudentStatus;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.ResourceNotFoundException;
import com.codsoft.sms.mapper.StudentMapper;
import com.codsoft.sms.repository.CourseRepository;
import com.codsoft.sms.repository.StudentRepository;
import com.codsoft.sms.service.StudentService;
import com.codsoft.sms.util.FileStorageUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link StudentService}.
 *
 * <p><strong>Transactions:</strong>
 * <ul>
 *   <li>Class-level {@code @Transactional(readOnly = true)} applies to all query methods —
 *       the JPA provider can skip dirty-checking on read-only transactions.</li>
 *   <li>Write methods override with {@code @Transactional} (full read-write) to ensure
 *       atomicity: uniqueness checks and repository saves happen in one transaction.</li>
 * </ul>
 *
 * <p><strong>Dynamic search:</strong> {@link #getStudents(String, Long, StudentStatus, Pageable)}
 * composes a JPA {@link Specification} at runtime rather than relying on a static
 * {@code findBy...} method chain. This approach lets the filter criteria grow (courseId,
 * status, search term) without adding new repository methods.
 *
 * <p><strong>Constructor injection</strong> is used exclusively — field injection
 * is prohibited per coding standards.
 */
@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FileStorageUtil fileStorageUtil;

    public StudentServiceImpl(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              FileStorageUtil fileStorageUtil) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    // -------------------------------------------------------------------------
    // Queries (read-only transaction inherited from class-level annotation)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Builds a {@link Specification} by combining only the predicates whose
     * filter value was supplied — all predicates are ANDed together. Criteria not
     * supplied are simply omitted (no "match all" wildcard predicate needed).
     *
     * <p>The search term is wrapped in {@code %term%} for a LIKE match against
     * {@code first_name}, {@code last_name}, {@code email}, and {@code roll_number}.
     */
    @Override
    public PagedResponse<StudentResponseDTO> getStudents(
            String search, Long courseId, StudentStatus status, Pageable pageable) {

        Specification<Student> spec = buildSearchSpecification(search, courseId, status);
        Page<Student> page = studentRepository.findAll(spec, pageable);

        List<StudentResponseDTO> content = page.getContent()
                .stream()
                .map(StudentMapper::toResponseDTO)
                .toList();

        return PagedResponse.from(page, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentOrThrow(id);
        return StudentMapper.toResponseDTO(student);
    }

    // -------------------------------------------------------------------------
    // Commands (read-write transaction — overrides class-level readOnly=true)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Uniqueness is checked before the INSERT:
     * <ol>
     *   <li>Email uniqueness — 409 if duplicate</li>
     *   <li>Roll-number uniqueness — 409 if duplicate</li>
     *   <li>Course existence — 404 if courseId not found</li>
     * </ol>
     */
    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        checkEmailUniqueness(requestDTO.getEmail(), null);
        checkRollNumberUniqueness(requestDTO.getRollNumber(), null);

        Course course = findCourseOrThrow(requestDTO.getCourseId());
        Student student = StudentMapper.toEntity(requestDTO, course);
        Student saved = studentRepository.save(student);
        return StudentMapper.toResponseDTO(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsForExport(String search, Long courseId, StudentStatus status, Sort sort) {
        Specification<Student> spec = buildSearchSpecification(search, courseId, status);
        // Cap export at 10,000 rows as per SDD section 7.11 to prevent OOM
        Pageable exportPageable = PageRequest.of(0, 10000, sort);
        
        Page<Student> studentPage = studentRepository.findAll(spec, exportPageable);
        
        return studentPage.getContent().stream()
                .map(StudentMapper::toResponseDTO)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uniqueness is checked with the current student's ID excluded so that
     * a student's unchanged email/roll-number does not trigger a false 409.
     */
    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        Student existing = findStudentOrThrow(id);

        checkEmailUniqueness(requestDTO.getEmail(), id);
        checkRollNumberUniqueness(requestDTO.getRollNumber(), id);

        Course course = findCourseOrThrow(requestDTO.getCourseId());
        StudentMapper.updateEntity(requestDTO, existing, course);
        // No explicit save() call needed — the managed entity is dirty-checked
        // and flushed automatically at transaction commit.
        return StudentMapper.toResponseDTO(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public StudentResponseDTO updateStudentStatus(Long id, StudentStatus status) {
        Student student = findStudentOrThrow(id);
        student.setStatus(status);
        return StudentMapper.toResponseDTO(student);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudentOrThrow(id);
        
        // Remove file if exists
        if (student.getProfileImageUrl() != null) {
            fileStorageUtil.deleteFile(student.getProfileImageUrl());
        }
        
        studentRepository.delete(student);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public StudentResponseDTO uploadProfileImage(Long id, MultipartFile file) {
        Student student = findStudentOrThrow(id);

        // Delete the old image if it exists to save space
        if (student.getProfileImageUrl() != null) {
            fileStorageUtil.deleteFile(student.getProfileImageUrl());
        }

        // Store the new image
        String fileUrl = fileStorageUtil.storeFile(file);
        student.setProfileImageUrl(fileUrl);
        // Transaction commit will flush the change

        return StudentMapper.toResponseDTO(student);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeProfileImage(Long id) {
        Student student = findStudentOrThrow(id);

        if (student.getProfileImageUrl() != null) {
            fileStorageUtil.deleteFile(student.getProfileImageUrl());
            student.setProfileImageUrl(null);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Loads a student by ID or throws {@link ResourceNotFoundException}.
     *
     * @param id the student primary key
     * @return the managed {@link Student} entity
     */
    private Student findStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    /**
     * Loads a course by ID or throws {@link ResourceNotFoundException}.
     *
     * @param courseId the course primary key
     * @return the managed {@link Course} entity
     */
    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    /**
     * Verifies that {@code email} is not already used by another student.
     *
     * @param email    the email to check
     * @param excludeId the ID of the student being updated ({@code null} on create)
     * @throws DuplicateResourceException if the email belongs to a different student
     */
    private void checkEmailUniqueness(String email, Long excludeId) {
        boolean duplicate = (excludeId == null)
                ? studentRepository.existsByEmail(email)
                : studentRepository.existsByEmailAndIdNot(email, excludeId);
        if (duplicate) {
            throw new DuplicateResourceException("Student", "email", email);
        }
    }

    /**
     * Verifies that {@code rollNumber} is not already used by another student.
     *
     * @param rollNumber the roll number to check
     * @param excludeId  the ID of the student being updated ({@code null} on create)
     * @throws DuplicateResourceException if the roll number belongs to a different student
     */
    private void checkRollNumberUniqueness(String rollNumber, Long excludeId) {
        boolean duplicate = (excludeId == null)
                ? studentRepository.existsByRollNumber(rollNumber)
                : studentRepository.existsByRollNumberAndIdNot(rollNumber, excludeId);
        if (duplicate) {
            throw new DuplicateResourceException("Student", "rollNumber", rollNumber);
        }
    }

    /**
     * Builds the composite {@link Specification} for the student list query.
     *
     * <p>Only predicates for non-null criteria are added — unused filters are
     * simply omitted rather than generating unnecessary {@code 1=1} clauses.
     *
     * @param search   optional free-text term (matched with LIKE)
     * @param courseId optional course filter
     * @param status   optional status filter
     * @return a combined specification (never {@code null}; returns match-all if all criteria null)
     */
    private Specification<Student> buildSearchSpecification(
            String search, Long courseId, StudentStatus status) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate firstName  = cb.like(cb.lower(root.get("firstName")),  pattern);
                Predicate lastName   = cb.like(cb.lower(root.get("lastName")),   pattern);
                Predicate email      = cb.like(cb.lower(root.get("email")),      pattern);
                Predicate rollNumber = cb.like(cb.lower(root.get("rollNumber")), pattern);
                predicates.add(cb.or(firstName, lastName, email, rollNumber));
            }

            if (courseId != null) {
                predicates.add(cb.equal(root.get("course").get("id"), courseId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
