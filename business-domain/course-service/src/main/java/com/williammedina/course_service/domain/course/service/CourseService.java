package com.williammedina.course_service.domain.course.service;

import com.williammedina.course_service.domain.course.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    List<CourseDTO> getAllCourses();

}
