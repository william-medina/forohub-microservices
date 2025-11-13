package com.williammedina.topic_read_service.domain.topicread.repository;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TopicReadRepository extends MongoRepository<TopicReadDocument, Long> {

    // Retrieve all topics
    Page<TopicReadDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Filter by courseId, keyword, and status
    @Query(value = "{ " +
            "'$and': [" +
            "  { $or: [ { 'course.id': ?0 }, { ?0: null } ] }," +
            "  { $or: [ { 'title': { $regex: ?1, $options: 'i' } }, { ?1: null } ] }," +
            "  { $or: [ { 'status': ?2 }, { ?2: null } ] }" +
            "]" +
            "}", sort = "{ 'createdAt': -1 }")
    Page<TopicReadDocument> findByFilters(Long courseId, String keyword, TopicReadDocument.Status status, Pageable pageable);

    // Filter by userId and keyword
    @Query(value = "{ 'author.id': ?0, 'title': { $regex: ?1, $options: 'i' } }", sort = "{ 'createdAt': -1 }")
    Page<TopicReadDocument> findByUserIdWithKeyword(Long userId, String keyword, Pageable pageable);

    // Topics by user, ordered by creation date
    Page<TopicReadDocument> findByAuthor_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find topics where a given user is a follower
    @Query(value = "{ 'followers.user.id': ?0 }", sort = "{ 'followers.followedAt': -1 }")
    Page<TopicReadDocument> findByFollowerUserId(Long userId, Pageable pageable);

    // Find topics where a given user is a follower and filter by keyword in title
    @Query(value = "{ 'followers.user.id': ?0, 'title': { $regex: ?1, $options: 'i' } }", sort = "{ 'followers.followedAt': -1 }")
    Page<TopicReadDocument> findByFollowerUserIdAndKeyword(Long userId, String keyword, Pageable pageable);

}
