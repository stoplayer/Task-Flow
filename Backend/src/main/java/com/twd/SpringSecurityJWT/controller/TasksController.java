package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Tasks;
import com.twd.SpringSecurityJWT.dto.TaskDTO;
import com.twd.SpringSecurityJWT.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/tasks")
public class TasksController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable int id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Tasks>> getAllTasks() {
        List<Tasks> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/addtask")
    public ResponseEntity<Map<String, Object>> addTask(@RequestBody TaskDTO taskDTO) {
        Tasks savedTask = taskService.createTask(taskDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task created successfully");
        response.put("task", savedTask);

        if (savedTask.getStatus() != null && savedTask.getStatus().toString().equals("COMPLETED")) {
            response.put("endtime", savedTask.getEndtime());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTaskById(@PathVariable int id, @RequestBody TaskDTO updatedTask) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Update the task
            Tasks task = taskService.updateTask(id, updatedTask);

            response.put("message", "Task updated successfully");
            response.put("task", task);

            // Add completion information if the task is completed
            if (task.getStatus() != null && task.getStatus().toString().equals("COMPLETED")) {
                response.put("completionTime", task.getEndtime());
                response.put("message", "Task marked as completed");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle exceptions and return an error response
            response.put("message", e.getMessage());
            return ResponseEntity.status(403).body(response); // HTTP 403 Forbidden
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteTaskById(@PathVariable int id) {
        taskService.deleteTask(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Task with ID " + id + " has been deleted successfully");
        response.put("status", "SUCCESS");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Map<String, Object>> getOverdueTasks() {
        List<Tasks> overdueTasks = taskService.getOverdueTasks();

        Map<String, Object> response = new HashMap<>();
        response.put("message", overdueTasks.isEmpty() ?
                "No overdue tasks found" :
                "Found " + overdueTasks.size() + " overdue tasks");
        response.put("tasks", overdueTasks);
        response.put("count", overdueTasks.size());

        return ResponseEntity.ok(response);
    }

    // Optional: Add a specific endpoint for completing tasks
    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable int id) {
        TaskDTO completionDTO = new TaskDTO();
        completionDTO.setStatus("COMPLETED");

        Tasks completedTask = taskService.updateTask(id, completionDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task marked as completed");
        response.put("task", completedTask);
        response.put("completionTime", completedTask.getEndtime());

        return ResponseEntity.ok(response);
    }
}