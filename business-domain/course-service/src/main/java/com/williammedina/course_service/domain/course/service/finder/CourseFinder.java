package com.williammedina.course_service.domain.course.service.finder;

import com.williammedina.course_service.domain.course.entity.CourseEntity;

public interface CourseFinder {

    CourseEntity findCourseById(Long courseId);

}
