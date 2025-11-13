package com.williammedina.course_service.domain.course.service;

import com.williammedina.course_service.domain.course.dto.CourseDTO;
import com.williammedina.course_service.domain.course.entity.CourseEntity;
import com.williammedina.course_service.domain.course.repository.CourseRepository;
import com.williammedina.course_service.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService, InternalCourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<CourseEntity> courses = courseRepository.findAllByOrderByNameAsc();
        return courses.stream().map(CourseDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long courseId) {
        CourseEntity course = findCourseById(courseId);
        return CourseDTO.fromEntity(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByIds(List<Long> ids) {
        List<CourseEntity> courses = courseRepository.findAllById(ids);
        return courses.stream().map(CourseDTO::fromEntity).toList();
    }

    public CourseEntity findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID: {}", courseId);
                    return new AppException("Curso no encontrado", HttpStatus.NOT_FOUND);
                });
    }

}
