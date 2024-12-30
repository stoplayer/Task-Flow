package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // Find comments by task ID
    List<Comment> findByTaskId(Integer taskId);

    // Find comments by user ID
    List<Comment> findByUserId(Integer userId);

    // Find comments containing specific content (case-insensitive)
    List<Comment> findByContentContainingIgnoreCase(String content);



    // Count comments for a specific task
    Long countByTaskId(Integer taskId);
}