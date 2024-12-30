package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.CommentDTO;
import com.twd.SpringSecurityJWT.dto.TaskDTO;
import com.twd.SpringSecurityJWT.entity.*;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.ProjectRepository;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServicetest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OurUserRepo ourUserRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createTask_shouldCreateTask() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setProjectId(1);
        taskDTO.setUserId(1);
        taskDTO.setPriority("HIGH");
        taskDTO.setStatus("PENDING");

        Project project = new Project();
        project.setId(1);

        OurUsers user = new OurUsers();
        user.setId(1);

        when(projectRepository.findById(taskDTO.getProjectId())).thenReturn(Optional.of(project));
        when(ourUserRepo.findById(taskDTO.getUserId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tasks result = taskService.createTask(taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(taskRepository, times(1)).save(result);
    }

    @Test
    void createTask_shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1);

        when(projectRepository.findById(taskDTO.getProjectId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDTO));
        assertEquals("Project not found with ID: 1", exception.getMessage());
    }

    @Test
    void createTask_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1);
        taskDTO.setUserId(1);

        Project project = new Project();
        project.setId(1);

        when(projectRepository.findById(taskDTO.getProjectId())).thenReturn(Optional.of(project));
        when(ourUserRepo.findById(taskDTO.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDTO));
        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void createTask_shouldSetDefaultStatusWhenStatusIsNull() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setProjectId(1);
        taskDTO.setUserId(1);
        taskDTO.setPriority("HIGH");
        taskDTO.setStatus(null);

        Project project = new Project();
        project.setId(1);

        OurUsers user = new OurUsers();
        user.setId(1);

        when(projectRepository.findById(taskDTO.getProjectId())).thenReturn(Optional.of(project));
        when(ourUserRepo.findById(taskDTO.getUserId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tasks result = taskService.createTask(taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
    }

    @Test
    void updateTask_shouldUpdateTask() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setPriority("MEDIUM");
        taskDTO.setStatus("IN_PROGRESS");

        Tasks task = new Tasks();
        task.setId(1);
        task.setName("Old Task");
        task.setDescription("Old Description");
        task.setUser(new OurUsers());
        task.getUser().setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tasks result = taskService.updateTask(1, taskDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Task", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(taskRepository, times(1)).save(result);
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Updated Task");

        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(1, taskDTO));
        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateTask_shouldThrowExceptionWhenUserNotAllowed() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Updated Task");

        Tasks task = new Tasks();
        task.setId(1);
        task.setUser(new OurUsers());
        task.getUser().setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other@example.com");
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(1, taskDTO));
        assertEquals("You are not allowed to update this task", exception.getMessage());
    }

    @Test
    void updateTask_shouldSetEndtimeWhenTaskIsCompleted() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setStatus("COMPLETED");

        Tasks task = new Tasks();
        task.setId(1);
        task.setUser(new OurUsers());
        task.getUser().setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tasks result = taskService.updateTask(1, taskDTO);

        // Assert
        assertNotNull(result.getEndtime());
        verify(taskRepository, times(1)).save(result);
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        // Arrange
        List<Tasks> tasksList = Arrays.asList(new Tasks(), new Tasks());
        when(taskRepository.findAll()).thenReturn(tasksList);

        // Act
        List<Tasks> result = taskService.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_shouldReturnTaskDTO() {
        // Arrange
        Tasks task = new Tasks();
        task.setId(1);
        task.setName("Test Task");
        task.setDescription("Test Description");

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        // Act
        TaskDTO result = taskService.getTaskById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Task", result.getName());
    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.getTaskById(1));
        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void deleteTask_shouldDeleteTaskById() {
        // Arrange
        when(taskRepository.existsById(1)).thenReturn(true);

        // Act
        taskService.deleteTask(1);

        // Assert
        verify(taskRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        when(taskRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(1));
        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void getOverdueTasks_shouldReturnOverdueTasks() {
        // Arrange
        Tasks task1 = new Tasks();
        task1.setEstimatedEndtime(LocalDateTime.now().minusDays(1));

        Tasks task2 = new Tasks();
        task2.setEstimatedEndtime(LocalDateTime.now().plusDays(4));

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // Act
        List<Tasks> result = taskService.getOverdueTasks();

        // Assert
        assertEquals(1, result.size());
        assertEquals(task1, result.get(0));
    }

    @Test
    void parseStatus_shouldThrowExceptionWhenInvalidStatus() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.parseStatus("INVALID"));
        assertEquals("Invalid status: INVALID", exception.getMessage());
    }

    @Test
    void parsePriority_shouldThrowExceptionWhenInvalidPriority() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.parsePriority("INVALID"));
        assertEquals("Invalid priority: INVALID", exception.getMessage());
    }
}