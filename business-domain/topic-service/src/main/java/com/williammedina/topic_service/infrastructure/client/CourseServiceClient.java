package com.williammedina.topic_service.infrastructure.client;

import com.williammedina.topic_service.domain.topic.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "course-service", path = "/internal/course")
public interface CourseServiceClient {

    @GetMapping("/{courseId}")
    CourseDTO getCourseById(@PathVariable("courseId") Long id);

    @GetMapping("/batch")
    List<CourseDTO> getCoursesByIds(@RequestParam List<Long> ids);

}
