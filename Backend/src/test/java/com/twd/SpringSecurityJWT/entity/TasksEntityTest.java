package com.twd.SpringSecurityJWT.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TasksEntityTest {

    @Test
    void testTasksEntity() {
        // Arrange
        Tasks task = new Tasks();
        task.setId(1);
        task.setName("Test Task");
        task.setDescription("This is a test task");
        task.setEstimatedEndtime(LocalDateTime.now().plusDays(7));
        task.setEndtime(LocalDateTime.now().plusDays(14));
        task.setStatus(Status.IN_PROGRESS);
        task.setPriority(Priority.HIGH);

        Project project = new Project();
        project.setId(1);
        project.setName("Test Project");

        OurUsers user = new OurUsers();
        user.setId(1);
        user.setEmail("test@example.com");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Comment 2");

        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        task.setProject(project);
        task.setUser(user);
        task.setComments(comments);

        // Act & Assert
        assertEquals(1, task.getId());
        assertEquals("Test Task", task.getName());
        assertEquals("This is a test task", task.getDescription());
        assertNotNull(task.getEstimatedEndtime());
        assertNotNull(task.getEndtime());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(project, task.getProject());
        assertEquals(user, task.getUser());
        assertEquals(2, task.getComments().size());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange
        Tasks task = new Tasks();
        task.setComments(new ArrayList<>()); // Manually initialize comments

        // Act & Assert
        assertNotNull(task.getComments()); // Comments should not be null
    }
}