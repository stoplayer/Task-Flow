package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Comment;
import com.twd.SpringSecurityJWT.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/addcomment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        Comment createdComment = commentService.createComment(
                commentRequest.getContent(),
                commentRequest.getTaskId(),
                commentRequest.getUserId()
        );
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/getallcomments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Comment> getCommentById(@PathVariable Integer id) {
        Comment comment = commentService.getCommentById(id);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

class CommentRequest {
    private String content;
    private Integer taskId;
    private Integer userId;

    // Getters and setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

}