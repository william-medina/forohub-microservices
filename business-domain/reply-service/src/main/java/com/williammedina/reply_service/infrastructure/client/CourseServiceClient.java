package com.williammedina.reply_service.infrastructure.client;

import com.williammedina.reply_service.domain.reply.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", path = "/internal/course")
public interface CourseServiceClient {

    @GetMapping("/{courseId}")
    CourseDTO getCourseById(@PathVariable("courseId") Long id);

}
