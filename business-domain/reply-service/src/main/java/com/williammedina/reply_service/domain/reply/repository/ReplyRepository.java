package com.williammedina.reply_service.domain.reply.repository;

import com.williammedina.reply_service.domain.reply.dto.ReplyCountDTO;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    List<ReplyEntity> findByTopicIdAndIsDeletedFalseOrderByCreatedAtDesc(Long topicId);

    @Query("SELECT r FROM Reply r " +
            "WHERE r.userId = :userId " +
            "AND r.isDeleted = false " +
            "ORDER BY r.createdAt DESC")
    Page<ReplyEntity> findByUserIdSorted(Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reply r " +
            "WHERE r.userId = :userId " +
            "AND r.isDeleted = false")
    long countByUserId(@Param("userId") Long userId);

    Optional<ReplyEntity> findByIdAndIsDeletedFalse(Long id);

    Long countByTopicIdAndIsDeletedFalse(Long topicId);

    @Query("SELECT new com.williammedina.reply_service.domain.reply.dto.ReplyCountDTO(r.topicId, COUNT(r)) " +
            "FROM Reply r " +
            "WHERE r.topicId IN :topicIds AND r.isDeleted = false " +
            "GROUP BY r.topicId")
    List<ReplyCountDTO> countRepliesByTopicIds(@Param("topicIds") List<Long> topicIds);

}
