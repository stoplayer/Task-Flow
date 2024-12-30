package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Project;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.entity.Status;  // Correctly importing Status enum
import com.twd.SpringSecurityJWT.repository.ProjectRepository;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OurUserRepo userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private LocalDateTime threeDaysFromNow;

    // Get a project by ID
    public Project getProjectById(int id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    // Get all projects
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Add a new project
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    // Update a project by ID
    public Project updateProjectById(int id, Project updatedProject) {
        // Retrieve the existing project
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        // Update only the fields explicitly provided in the request body
        existingProject.setName(updatedProject.getName());
        existingProject.setBudget(updatedProject.getBudget());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setEstimatedEndtime(updatedProject.getEstimatedEndtime());
        existingProject.setEndtime(updatedProject.getEndtime());

        // Retain the existing team
        // The team in the database remains unchanged if not passed in the request body

        // Save and return the updated project
        return projectRepository.save(existingProject);
    }


    // Delete a project by ID
    public void deleteProjectById(int id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
    }

    // Add a user to project team
    public Project addTeamMember(int projectId, int userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        OurUsers user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!project.getTeam().contains(user)) {
            project.getTeam().add(user);
            return projectRepository.save(project);
        }
        return project;
    }

    // Remove a user from project team
    public Project removeTeamMember(int projectId, int userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        OurUsers user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        project.getTeam().remove(user);
        return projectRepository.save(project);
    }

    // Get all team members of a project
    public List<OurUsers> getProjectTeam(int projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        return project.getTeam();
    }

    // Retrieve overdue projects (estimatedEndtime in 3 days)
    public List<Project> getOverdueProjects(LocalDateTime threeDaysFromNow) {
        this.threeDaysFromNow = threeDaysFromNow;
        LocalDateTime now = LocalDateTime.now();
        threeDaysFromNow = now.plusDays(3);
        return projectRepository.findByEstimatedEndtimeBefore(threeDaysFromNow);
    }

    // Calculate the progress of a project (based on the number of completed tasks)
    public double calculateProjectProgress(int projectId) {
        // Retrieve all tasks associated with the project
        List<Tasks> tasks = taskRepository.findByProjectId(projectId);

        if (tasks.isEmpty()) {
            return 0.0; // No tasks, so no progress
        }

        // Count the number of completed tasks
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == Status.COMPLETED)  // Correctly using Status enum for filtering completed tasks
                .count();

        // Calculate the progress percentage
        double progress = ((double) completedTasks / tasks.size()) * 100;
        return progress;
    }


    // Set endtime for completed projects
    public void completeProject(int projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Check if all tasks are completed
        List<Tasks> tasks = project.getTasks();
        boolean allTasksCompleted = tasks.stream().allMatch(task -> task.getStatus() == Status.COMPLETED);

        if (allTasksCompleted) {
            project.setStatus(Status.COMPLETED);
            project.setEndtime(LocalDateTime.now());  // Set current time as endtime for completed projects
            projectRepository.save(project);
        } else {
            throw new RuntimeException("Cannot complete project. Not all tasks are completed.");
        }
    }

    public void updateProjectStatus(int projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        List<Tasks> tasks = project.getTasks();
        boolean allTasksCompleted = tasks.stream().allMatch(task -> task.getStatus() == Status.COMPLETED);

        if (allTasksCompleted && project.getStatus() != Status.COMPLETED) {
            project.setStatus(Status.COMPLETED);
            project.setEndtime(LocalDateTime.now());
        } else if (!allTasksCompleted && project.getStatus() == Status.COMPLETED) {
            project.setStatus(Status.IN_PROGRESS);
            project.setEndtime(null);
        }

        projectRepository.save(project);
    }
}

