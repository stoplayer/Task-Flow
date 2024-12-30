package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.CommentDTO;
import com.twd.SpringSecurityJWT.entity.*;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.TaskRepository;
import com.twd.SpringSecurityJWT.repository.ProjectRepository;
import com.twd.SpringSecurityJWT.dto.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final OurUserRepo ourUserRepo;

    // Create a new task
    public Tasks createTask(TaskDTO taskDTO) {
        // Find the project by ID
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + taskDTO.getProjectId()));

        // Find the user by ID
        OurUsers user = ourUserRepo.findById(taskDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + taskDTO.getUserId()));

        // Set the default status if not provided
        Status status = (taskDTO.getStatus() == null || taskDTO.getStatus().isEmpty())
                ? Status.PENDING // Default value
                : parseStatus(taskDTO.getStatus());

        // Parse the priority
        Priority priority = parsePriority(taskDTO.getPriority());

        // Create a new task and set its properties
        Tasks task = new Tasks();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setEstimatedEndtime(taskDTO.getEstimatedEndtime());
        task.setStatus(status);
        task.setPriority(priority);
        task.setProject(project);
        task.setUser(user);

        // If the task is completed, set the endtime to the current local time
        if (status == Status.COMPLETED) {
            task.setEndtime(LocalDateTime.now());
        }

        // Save the task and update project status if needed
        task = taskRepository.save(task);
        updateProjectStatus(project);

        return task;
    }

    // Update an existing task
    public Tasks updateTask(int id, TaskDTO taskDTO) {
        // Fetch the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Find the task by ID
        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // Check if the current user is the creator of the task
        if (task.getUser() == null || !task.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You are not allowed to update this task");
        }

        // Parse the status and priority
        Status status = parseStatus(taskDTO.getStatus());
        Priority priority = parsePriority(taskDTO.getPriority());

        // Update task fields
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setEstimatedEndtime(taskDTO.getEstimatedEndtime());
        task.setStatus(status);
        task.setPriority(priority);

        // If the task is completed, set the endtime to the current local time
        if (status == Status.COMPLETED && task.getEndtime() == null) {
            task.setEndtime(LocalDateTime.now());
        }

        // Save the updated task and update project status if needed
        task = taskRepository.save(task);
        updateProjectStatus(task.getProject());

        return task;
    }


    // Method to update project status based on tasks' statuses
    private void updateProjectStatus(Project project) {
        List<Tasks> tasks = project.getTasks(); // Directly using the tasks list from the project entity

        // Check if all tasks are completed
        boolean allCompleted = tasks.stream().allMatch(task -> task.getStatus() == Status.COMPLETED);

        // Check if any task is completed
        boolean anyInProgress = tasks.stream().anyMatch(task -> task.getStatus() == Status.IN_PROGRESS);

        // Determine the project status
        if (allCompleted) {
            project.setStatus(Status.COMPLETED);
        } else if (anyInProgress) {
            project.setStatus(Status.IN_PROGRESS);
        } else {
            project.setStatus(Status.PENDING);
        }

        projectRepository.save(project);  // Save the updated project status
    }

    // Get all tasks
    public List<Tasks> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get a task by ID
    public TaskDTO getTaskById(int id) {
        // Fetch the task by ID
        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // Map task entity to TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setName(task.getName());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setEstimatedEndtime(task.getEstimatedEndtime());
        taskDTO.setEndtime(task.getEndtime());
        taskDTO.setStatus(task.getStatus() != null ? task.getStatus().toString() : null);
        taskDTO.setPriority(task.getPriority() != null ? task.getPriority().toString() : null);

        // Include project details
        if (task.getProject() != null) {
            taskDTO.setProjectId(task.getProject().getId());
            taskDTO.setProjectName(task.getProject().getName());
        }

        // Include user details
        if (task.getUser() != null) {
            taskDTO.setUserId(task.getUser().getId());
            taskDTO.setUserName(task.getUser().getName());
            taskDTO.setUserEmail(task.getUser().getEmail());
            taskDTO.setUserRole(task.getUser().getRole());
        }

        // Include comments
        List<CommentDTO> commentDTOs = task.getComments().stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setContent(comment.getContent());
            commentDTO.setUserName(comment.getUser().getName());
            commentDTO.setUserEmail(comment.getUser().getEmail());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            return commentDTO;
        }).collect(Collectors.toList());
        taskDTO.setComments(commentDTOs);

        return taskDTO;
    }


    // Delete a task by ID
    public void deleteTask(int id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Retrieve overdue tasks within 3 days
    public List<Tasks> getOverdueTasks() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime thresholdDateTime = currentDateTime.plusDays(3);

        return taskRepository.findAll().stream()
                .filter(task -> task.getEstimatedEndtime() != null && task.getEstimatedEndtime().isBefore(thresholdDateTime))
                .collect(Collectors.toList());
    }

    // Helper method to parse the status string to Status enum
    public Status parseStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    // Helper method to parse the priority string to Priority enum
    public Priority parsePriority(String priority) {
        try {
            return Priority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid priority: " + priority);
        }
    }
}
