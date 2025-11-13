package com.williammedina.topic_service.domain.topicfollow.repository;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopicFollowRepository extends JpaRepository<TopicFollowEntity, Long> {

    boolean existsByUserIdAndTopicIdAndTopicIsDeletedFalse(Long userId, Long topicId);

    void deleteByUserIdAndTopicId(Long userId, Long topicId);

    long countByUserIdAndTopicIsDeletedFalse(Long userId);

    @Query("SELECT t FROM TopicFollow t " +
            "WHERE t.userId = :userId " +
            "AND t.topic.isDeleted = false " +
            "AND (:keyword IS NULL OR t.topic.title LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.followedAt DESC")
    Page<TopicFollowEntity> findByUserIdWithKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT t FROM TopicFollow t " +
            "WHERE t.userId = :userId " +
            "AND t.topic.isDeleted = false " +
            "ORDER BY t.followedAt DESC")
    Page<TopicFollowEntity> findByUserIdSorted(Long userId, Pageable pageable);

    Optional<TopicFollowEntity> findByUserIdAndTopic(Long userId, TopicEntity topic);

}
