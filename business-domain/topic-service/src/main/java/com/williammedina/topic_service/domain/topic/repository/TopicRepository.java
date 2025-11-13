package com.williammedina.topic_service.domain.topic.repository;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    List<TopicEntity> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    // Retrieve all topics that are not deleted with Pageable
    Page<TopicEntity> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // Filter by courseId, keyword, and status
    @Query("SELECT t FROM Topic t " +
            "WHERE t.isDeleted = false " +
            "AND (:courseId IS NULL OR t.courseId = :courseId) " +
            "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY t.createdAt DESC")
    Page<TopicEntity> findByFilters(
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            @Param("status") TopicEntity.Status status,
            Pageable pageable
    );

    // Filter by userId and keyword
    @Query("SELECT t FROM Topic t " +
            "WHERE t.isDeleted = false " +
            "AND t.userId = :userId " +
            "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.createdAt DESC")
    Page<TopicEntity> findByUserIdWithKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Topics by user, ordered by creation date
    Page<TopicEntity> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Check existence by title or description
    boolean existsByTitleAndIsDeletedFalse(String title);
    boolean existsByDescriptionAndIsDeletedFalse(String description);

    // Count topics by user
    long countByUserIdAndIsDeletedFalse(Long userId);

    // Find by id if not deleted
    Optional<TopicEntity> findByIdAndIsDeletedFalse(Long topicId);
}
