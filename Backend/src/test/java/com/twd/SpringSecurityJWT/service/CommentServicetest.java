package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Comment;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.repository.CommentRepository;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentServicetest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OurUserRepo ourUsersRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void testCreateComment_validInput() {
        // Arrange
        String content = "This is a comment";
        int taskId = 1;
        Integer userId = 1;

        Tasks task = new Tasks();
        task.setId(taskId);

        OurUsers user = new OurUsers();
        user.setId(userId);

        Comment expectedComment = new Comment(content, task, user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(ourUsersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

        // Act
        Comment actualComment = commentService.createComment(content, taskId, userId);

        // Assert
        assertNotNull(actualComment);
        assertEquals(content, actualComment.getContent()); // String vs. String
        assertEquals(taskId, actualComment.getTask().getId()); // Long vs. Long
        assertEquals(userId, actualComment.getUser().getId()); // Long vs. Long
        assertNotNull(actualComment.getCreatedAt()); // Ensure createdAt is set
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testCreateComment_taskNotFound() {
        // Arrange
        String content = "This is a comment";
        Long taskId = 1L;
        Long userId = 1L;

        when(taskRepository.findById(Math.toIntExact(taskId))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.createComment(content, Math.toIntExact(taskId), Math.toIntExact(userId));
        });

        assertEquals("Task not found", exception.getMessage()); // String vs. String
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testCreateComment_userNotFound() {
        // Arrange
        String content = "This is a comment";
        Long taskId = 1L;
        Long userId = 1L;

        Tasks task = new Tasks();
        task.setId(Math.toIntExact(taskId));

        when(taskRepository.findById(Math.toIntExact(taskId))).thenReturn(Optional.of(task));
        when(ourUsersRepository.findById(Math.toIntExact(userId))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.createComment(content, Math.toIntExact(taskId), Math.toIntExact(userId));
        });

        assertEquals("User not found", exception.getMessage()); // String vs. String
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testGetAllComments() {
        // Arrange
        Comment comment1 = new Comment("Comment 1", new Tasks(), new OurUsers());
        Comment comment2 = new Comment("Comment 2", new Tasks(), new OurUsers());

        when(commentRepository.findAll()).thenReturn(List.of(comment1, comment2));

        // Act
        List<Comment> comments = commentService.getAllComments();

        // Assert
        assertNotNull(comments);
        assertEquals(2, comments.size()); // int vs. int
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void testGetCommentById_found() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment("Test comment", new Tasks(), new OurUsers());
        comment.setId(commentId);

        when(commentRepository.findById(Math.toIntExact(commentId))).thenReturn(Optional.of(comment));

        // Act
        Comment foundComment = commentService.getCommentById(Math.toIntExact(commentId));

        // Assert
        assertNotNull(foundComment);
        assertEquals(commentId, foundComment.getId()); // Long vs. Long
        verify(commentRepository, times(1)).findById(Math.toIntExact(commentId));
    }

    @Test
    void testGetCommentById_notFound() {
        // Arrange
        Long commentId = 1L;

        when(commentRepository.findById(Math.toIntExact(commentId))).thenReturn(Optional.empty());

        // Act
        Comment foundComment = commentService.getCommentById(Math.toIntExact(commentId));

        // Assert
        assertNull(foundComment); // Correct comparison: Checking if it's null
        verify(commentRepository, times(1)).findById(Math.toIntExact(commentId));
    }

    @Test
    void testDeleteComment() {
        // Arrange
        Long commentId = 1L;

        doNothing().when(commentRepository).deleteById(Math.toIntExact(commentId));

        // Act
        commentService.deleteComment(Math.toIntExact(commentId));

        // Assert
        verify(commentRepository, times(1)).deleteById(Math.toIntExact(commentId));
    }
}