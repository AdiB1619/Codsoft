package com.codsoft.sms.service.impl;

import com.codsoft.sms.dto.request.CourseRequestDTO;
import com.codsoft.sms.dto.response.CourseResponseDTO;
import com.codsoft.sms.entity.Course;
import com.codsoft.sms.exception.DuplicateResourceException;
import com.codsoft.sms.exception.ResourceNotFoundException;
import com.codsoft.sms.mapper.CourseMapper;
import com.codsoft.sms.repository.CourseRepository;
import com.codsoft.sms.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link CourseService}.
 *
 * <p><strong>Transactions:</strong>
 * <ul>
 *   <li>Read methods run in a read-only transaction to allow query optimisations.</li>
 *   <li>Write methods run in a read-write transaction to ensure atomicity.</li>
 * </ul>
 *
 * <p><strong>Constructor injection</strong> is used exclusively — field injection
 * is prohibited per coding standards.
 */
@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // -------------------------------------------------------------------------
    // Queries (read-only transaction inherited from class-level annotation)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves all courses ordered by their natural DB order (insertion order for
     * small master-data tables). The list is mapped to DTOs before returning.
     */
    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseMapper::toResponseDTO)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return CourseMapper.toResponseDTO(course);
    }

    // -------------------------------------------------------------------------
    // Commands (read-write transaction — overrides class-level readOnly=true)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Uniqueness of {@code courseCode} is checked before the INSERT to provide
     * a clear {@code 409 Conflict} rather than a raw
     * {@code DataIntegrityViolationException} from the database.
     */
    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        if (courseRepository.existsByCourseCode(requestDTO.getCourseCode())) {
            throw new DuplicateResourceException("Course", "courseCode", requestDTO.getCourseCode());
        }
        Course course = CourseMapper.toEntity(requestDTO);
        Course saved = courseRepository.save(course);
        return CourseMapper.toResponseDTO(saved);
    }
}
