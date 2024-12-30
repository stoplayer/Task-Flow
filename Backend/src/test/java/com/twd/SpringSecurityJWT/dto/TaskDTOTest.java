package com.twd.SpringSecurityJWT.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();

        // Act
        taskDTO.setId(1);
        taskDTO.setName("Task 1");
        taskDTO.setDescription("This is a test task");
        taskDTO.setEstimatedEndtime(LocalDateTime.of(2024, 12, 31, 23, 59));
        taskDTO.setEndtime(LocalDateTime.of(2024, 12, 31, 23, 59));
        taskDTO.setStatus("In Progress");
        taskDTO.setPriority("High");
        taskDTO.setProjectId(101);
        taskDTO.setProjectName("Project Alpha");
        taskDTO.setUserId(201);
        taskDTO.setUserName("John Doe");
        taskDTO.setUserEmail("john.doe@example.com");
        taskDTO.setUserRole("USER");

        CommentDTO commentDTO = new CommentDTO();
        taskDTO.setComments(List.of(commentDTO));

        // Assert
        assertThat(taskDTO.getId()).isEqualTo(1);
        assertThat(taskDTO.getName()).isEqualTo("Task 1");
        assertThat(taskDTO.getDescription()).isEqualTo("This is a test task");
        assertThat(taskDTO.getEstimatedEndtime()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        assertThat(taskDTO.getEndtime()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        assertThat(taskDTO.getStatus()).isEqualTo("In Progress");
        assertThat(taskDTO.getPriority()).isEqualTo("High");
        assertThat(taskDTO.getProjectId()).isEqualTo(101);
        assertThat(taskDTO.getProjectName()).isEqualTo("Project Alpha");
        assertThat(taskDTO.getUserId()).isEqualTo(201);
        assertThat(taskDTO.getUserName()).isEqualTo("John Doe");
        assertThat(taskDTO.getUserEmail()).isEqualTo("john.doe@example.com");
        assertThat(taskDTO.getUserRole()).isEqualTo("USER");
        assertThat(taskDTO.getComments()).containsExactly(commentDTO);
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TaskDTO taskDTO1 = new TaskDTO();
        taskDTO1.setId(1);
        taskDTO1.setName("Task 1");

        TaskDTO taskDTO2 = new TaskDTO();
        taskDTO2.setId(1);
        taskDTO2.setName("Task 1");

        TaskDTO taskDTO3 = new TaskDTO();
        taskDTO3.setId(2);
        taskDTO3.setName("Task 2");

        // Act & Assert
        assertThat(taskDTO1).isEqualTo(taskDTO2);
        assertThat(taskDTO1.hashCode()).isEqualTo(taskDTO2.hashCode());

        assertThat(taskDTO1).isNotEqualTo(taskDTO3);
        assertThat(taskDTO1.hashCode()).isNotEqualTo(taskDTO3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1);
        taskDTO.setName("Task 1");

        // Act
        String toStringResult = taskDTO.toString();

        // Assert
        assertThat(toStringResult).contains("id=1");
        assertThat(toStringResult).contains("name=Task 1");
    }
}