package com.twd.SpringSecurityJWT.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest {

    @Test
    void testProjectEntity() {
        // Arrange
        Project project = new Project();
        project.setId(1);
        project.setName("Test Project");
        project.setBudget(10000);
        project.setDescription("This is a test project");
        project.setEstimatedEndtime(LocalDateTime.now().plusDays(30));
        project.setEndtime(LocalDateTime.now().plusDays(60));
        project.setStatus(Status.IN_PROGRESS);

        OurUsers user1 = new OurUsers();
        user1.setId(1);
        user1.setEmail("user1@example.com");

        OurUsers user2 = new OurUsers();
        user2.setId(2);
        user2.setEmail("user2@example.com");

        List<OurUsers> team = new ArrayList<>();
        team.add(user1);
        team.add(user2);

        project.setTeam(team);

        Tasks task1 = new Tasks();
        task1.setId(1);
        task1.setName("Task 1");

        Tasks task2 = new Tasks();
        task2.setId(2);
        task2.setName("Task 2");

        List<Tasks> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        project.setTasks(tasks);

        // Act & Assert
        assertEquals(1, project.getId());
        assertEquals("Test Project", project.getName());
        assertEquals(10000, project.getBudget());
        assertEquals("This is a test project", project.getDescription());
        assertNotNull(project.getEstimatedEndtime());
        assertNotNull(project.getEndtime());
        assertEquals(Status.IN_PROGRESS, project.getStatus());
        assertEquals(2, project.getTeam().size());
        assertEquals(2, project.getTasks().size());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange
        Project project = new Project();

        // Act & Assert
        assertNotNull(project.getTeam()); // Team should be initialized
        assertNotNull(project.getTasks()); // Tasks should be initialized
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        OurUsers user1 = new OurUsers();
        user1.setId(1);

        OurUsers user2 = new OurUsers();
        user2.setId(2);

        List<OurUsers> team = new ArrayList<>();
        team.add(user1);
        team.add(user2);

        Tasks task1 = new Tasks();
        task1.setId(1);

        Tasks task2 = new Tasks();
        task2.setId(2);

        List<Tasks> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        Project project = new Project(1, "Test Project", 10000, "This is a test project",
                LocalDateTime.now().plusDays(30), LocalDateTime.now().plusDays(60), Status.IN_PROGRESS, team, tasks);

        // Act & Assert
        assertEquals(1, project.getId());
        assertEquals("Test Project", project.getName());
        assertEquals(10000, project.getBudget());
        assertEquals("This is a test project", project.getDescription());
        assertNotNull(project.getEstimatedEndtime());
        assertNotNull(project.getEndtime());
        assertEquals(Status.IN_PROGRESS, project.getStatus());
        assertEquals(2, project.getTeam().size());
        assertEquals(2, project.getTasks().size());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Project project1 = new Project();
        project1.setId(1);
        project1.setName("Test Project");

        Project project2 = new Project();
        project2.setId(1);
        project2.setName("Test Project");

        // Act & Assert
        assertEquals(project1, project2); // Should be equal
        assertEquals(project1.hashCode(), project2.hashCode()); // Hash codes should match
    }

    @Test
    void testNotEquals() {
        // Arrange
        Project project1 = new Project();
        project1.setId(1);
        project1.setName("Test Project 1");

        Project project2 = new Project();
        project2.setId(2);
        project2.setName("Test Project 2");

        // Act & Assert
        assertNotEquals(project1, project2); // Should not be equal
        assertNotEquals(project1.hashCode(), project2.hashCode()); // Hash codes should not match
    }

    @Test
    void testToString() {
        // Arrange
        Project project = new Project();
        project.setId(1);
        project.setName("Test Project");

        // Act
        String toString = project.toString();

        // Assert
        assertTrue(toString.contains("Test Project")); // Should include the name
        assertTrue(toString.contains("1")); // Should include the ID
    }
}