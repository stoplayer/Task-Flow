package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.TaskDTO;
import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.entity.Status;
import com.twd.SpringSecurityJWT.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TasksControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TasksController tasksController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTaskById() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1);
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");

        when(taskService.getTaskById(1)).thenReturn(taskDTO);

        // Act
        ResponseEntity<TaskDTO> response = tasksController.getTaskById(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Task", response.getBody().getName());
        assertEquals("Test Description", response.getBody().getDescription());
        verify(taskService, times(1)).getTaskById(1);
    }

    @Test
    void testGetAllTasks() {
        // Arrange
        Tasks task1 = new Tasks();
        task1.setId(1);
        task1.setName("Task 1");
        task1.setDescription("Description 1");

        Tasks task2 = new Tasks();
        task2.setId(2);
        task2.setName("Task 2");
        task2.setDescription("Description 2");

        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        // Act
        ResponseEntity<List<Tasks>> response = tasksController.getAllTasks();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testAddTask() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("New Task");
        taskDTO.setDescription("New Description");

        Tasks savedTask = new Tasks();
        savedTask.setId(1);
        savedTask.setName("New Task");
        savedTask.setDescription("New Description");

        when(taskService.createTask(taskDTO)).thenReturn(savedTask);

        // Act
        ResponseEntity<Map<String, Object>> response = tasksController.addTask(taskDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Task created successfully", response.getBody().get("message"));
        assertEquals(savedTask, response.getBody().get("task"));
        verify(taskService, times(1)).createTask(taskDTO);
    }

    @Test
    void testUpdateTaskById_Success() {
        // Arrange
        TaskDTO updatedTask = new TaskDTO();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");

        Tasks task = new Tasks();
        task.setId(1);
        task.setName("Updated Task");
        task.setDescription("Updated Description");

        when(taskService.updateTask(1, updatedTask)).thenReturn(task);

        // Act
        ResponseEntity<Map<String, Object>> response = tasksController.updateTaskById(1, updatedTask);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Task updated successfully", response.getBody().get("message"));
        assertEquals(task, response.getBody().get("task"));
        verify(taskService, times(1)).updateTask(1, updatedTask);
    }

    @Test
    void testUpdateTaskById_Exception() {
        // Arrange
        TaskDTO updatedTask = new TaskDTO();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Description");

        when(taskService.updateTask(1, updatedTask)).thenThrow(new RuntimeException("Forbidden"));

        // Act
        ResponseEntity<Map<String, Object>> response = tasksController.updateTaskById(1, updatedTask);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Forbidden", response.getBody().get("message"));
        verify(taskService, times(1)).updateTask(1, updatedTask);
    }

    @Test
    void testDeleteTaskById() {
        // Arrange
        doNothing().when(taskService).deleteTask(1);

        // Act
        ResponseEntity<Map<String, String>> response = tasksController.deleteTaskById(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Task with ID 1 has been deleted successfully", response.getBody().get("message"));
        assertEquals("SUCCESS", response.getBody().get("status"));
        verify(taskService, times(1)).deleteTask(1);
    }

    @Test
    void testGetOverdueTasks() {
        // Arrange
        Tasks overdueTask = new Tasks();
        overdueTask.setId(1);
        overdueTask.setName("Overdue Task");
        overdueTask.setDescription("Overdue Description");

        when(taskService.getOverdueTasks()).thenReturn(Arrays.asList(overdueTask));

        // Act
        ResponseEntity<Map<String, Object>> response = tasksController.getOverdueTasks();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Found 1 overdue tasks", response.getBody().get("message"));
        assertEquals(1, response.getBody().get("count"));
        verify(taskService, times(1)).getOverdueTasks();
    }

    @Test
    void testCompleteTask() {
        // Arrange
        TaskDTO completionDTO = new TaskDTO();
        completionDTO.setStatus(String.valueOf(Status.COMPLETED));

        Tasks completedTask = new Tasks();
        completedTask.setId(1);
        completedTask.setName("Completed Task");
        completedTask.setDescription("Completed Description");
        completedTask.setEndtime(LocalDateTime.now());

        when(taskService.updateTask(1, completionDTO)).thenReturn(completedTask);

        // Act
        ResponseEntity<Map<String, Object>> response = tasksController.completeTask(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Task marked as completed", response.getBody().get("message"));
        assertEquals(completedTask, response.getBody().get("task"));
        assertNotNull(response.getBody().get("completionTime"));
        verify(taskService, times(1)).updateTask(1, completionDTO);
    }
}