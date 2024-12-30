package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Project;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.entity.Status;
import com.twd.SpringSecurityJWT.repository.ProjectRepository;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServicetest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OurUserRepo userRepository;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProjectById_Success() {
        Project project = new Project();
        project.setId(1);
        project.setName("Test Project");

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(1);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.getProjectById(1));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getAllProjects_Success() {
        List<Project> projects = List.of(new Project(), new Project());

        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void addProject_Success() {
        Project project = new Project();
        project.setName("New Project");

        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.addProject(project);

        assertNotNull(result);
        assertEquals("New Project", result.getName());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void updateProjectById_Success() {
        Project existingProject = new Project();
        existingProject.setId(1);
        existingProject.setName("Old Project");

        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");

        when(projectRepository.findById(1)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(existingProject);

        Project result = projectService.updateProjectById(1, updatedProject);

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    void updateProjectById_NotFound() {
        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");

        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.updateProjectById(1, updatedProject));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProjectById_Success() {
        when(projectRepository.existsById(1)).thenReturn(true);

        projectService.deleteProjectById(1);

        verify(projectRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteProjectById_NotFound() {
        when(projectRepository.existsById(1)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.deleteProjectById(1));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).deleteById(1);
    }

    @Test
    void addTeamMember_Success() {
        Project project = new Project();
        project.setId(1);
        project.setTeam(new ArrayList<>());

        OurUsers user = new OurUsers();
        user.setId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Project result = projectService.addTeamMember(1, 1);

        assertTrue(result.getTeam().contains(user));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void addTeamMember_ProjectNotFound() {
        OurUsers user = new OurUsers();
        user.setId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.addTeamMember(1, 1));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void addTeamMember_UserNotFound() {
        Project project = new Project();
        project.setId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.addTeamMember(1, 1));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void removeTeamMember_Success() {
        Project project = new Project();
        project.setId(1);
        project.setTeam(new ArrayList<>());

        OurUsers user = new OurUsers();
        user.setId(1);
        project.getTeam().add(user);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Project result = projectService.removeTeamMember(1, 1);

        assertFalse(result.getTeam().contains(user));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void removeTeamMember_ProjectNotFound() {
        OurUsers user = new OurUsers();
        user.setId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.removeTeamMember(1, 1));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void removeTeamMember_UserNotFound() {
        Project project = new Project();
        project.setId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.removeTeamMember(1, 1));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void getProjectTeam_Success() {
        Project project = new Project();
        project.setId(1);
        project.setTeam(new ArrayList<>());

        OurUsers user = new OurUsers();
        user.setId(1);
        project.getTeam().add(user);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        List<OurUsers> result = projectService.getProjectTeam(1);

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getProjectTeam_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.getProjectTeam(1));

        assertEquals("Project not found with ID: 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getOverdueProjects_Success() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        List<Project> projects = List.of(new Project(), new Project());

        when(projectRepository.findByEstimatedEndtimeBefore(threeDaysFromNow)).thenReturn(projects);

        List<Project> result = projectService.getOverdueProjects(threeDaysFromNow);

        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findByEstimatedEndtimeBefore(threeDaysFromNow);
    }

    @Test
    void calculateProjectProgress_Success() {
        Tasks task1 = new Tasks();
        task1.setStatus(Status.COMPLETED);

        Tasks task2 = new Tasks();
        task2.setStatus(Status.IN_PROGRESS);

        List<Tasks> tasks = List.of(task1, task2);

        when(taskRepository.findByProjectId(1)).thenReturn(tasks);

        double progress = projectService.calculateProjectProgress(1);

        assertEquals(50.0, progress);
        verify(taskRepository, times(1)).findByProjectId(1);
    }

    @Test
    void calculateProjectProgress_NoTasks() {
        when(taskRepository.findByProjectId(1)).thenReturn(List.of());

        double progress = projectService.calculateProjectProgress(1);

        assertEquals(0.0, progress);
        verify(taskRepository, times(1)).findByProjectId(1);
    }

    @Test
    void completeProject_Success() {
        Project project = new Project();
        project.setId(1);
        project.setTasks(new ArrayList<>());

        Tasks task = new Tasks();
        task.setStatus(Status.COMPLETED);
        project.getTasks().add(task);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.completeProject(1);

        assertEquals(Status.COMPLETED, project.getStatus());
        assertNotNull(project.getEndtime());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void completeProject_NotAllTasksCompleted() {
        Project project = new Project();
        project.setId(1);
        project.setTasks(new ArrayList<>());

        Tasks task = new Tasks();
        task.setStatus(Status.IN_PROGRESS);
        project.getTasks().add(task);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.completeProject(1));

        assertEquals("Cannot complete project. Not all tasks are completed.", exception.getMessage());
        verify(projectRepository, never()).save(project);
    }

    @Test
    void updateProjectStatus_AllTasksCompleted() {
        Project project = new Project();
        project.setId(1);
        project.setTasks(new ArrayList<>());
        project.setStatus(Status.IN_PROGRESS);

        Tasks task = new Tasks();
        task.setStatus(Status.COMPLETED);
        project.getTasks().add(task);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.updateProjectStatus(1);

        assertEquals(Status.COMPLETED, project.getStatus());
        assertNotNull(project.getEndtime());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void updateProjectStatus_NotAllTasksCompleted() {
        Project project = new Project();
        project.setId(1);
        project.setTasks(new ArrayList<>());
        project.setStatus(Status.COMPLETED);

        Tasks task = new Tasks();
        task.setStatus(Status.IN_PROGRESS);
        project.getTasks().add(task);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.updateProjectStatus(1);

        assertEquals(Status.IN_PROGRESS, project.getStatus());
        assertNull(project.getEndtime());
        verify(projectRepository, times(1)).save(project);
    }
}